package com.shi.effitask.worker.boot;

import com.alibaba.fastjson2.JSON;
import com.shi.effitask.constant.TaskConstant;
import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.enums.TaskStatus;
import com.shi.effitask.pojo.dto.TaskFilterDTO;
import com.shi.effitask.pojo.dto.TaskResp;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.utils.Utils;
import com.shi.effitask.worker.constant.WorkerConstant;
import com.shi.effitask.worker.core.TaskBuilder;
import com.shi.effitask.worker.core.base.TaskBase;
import com.shi.effitask.worker.core.base.TaskRet;
import com.shi.effitask.worker.core.base.TaskStageBase;
import com.shi.effitask.worker.core.observer.ObserverManager;
import com.shi.effitask.worker.enums.StageType;
import com.shi.effitask.worker.rpc.HTTPTaskManager;
import com.shi.effitask.worker.rpc.TaskManager;
import com.shi.effitask.worker.core.observer.StageObserver;
import com.shi.effitask.worker.user.custom.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerLaunch implements Launch {


    Logger LOGGER = LoggerFactory.getLogger(WorkerLaunch.class);

    public static int MaxConcurrentRunTimes = 5; // 线程池最大数量
    public static int concurrentRunTimes = MaxConcurrentRunTimes; // 线程并发数
    ThreadPoolExecutor threadPoolExecutor; // 拉取任务的线程池
    ScheduledExecutorService loadPool;

    //任务管理远程调用接口
    public final TaskManager taskManager;

    //要执行的类的包名
    public static String packageName;

    // 拉取哪几类任务
    static List<Class> taskTypes;

    // 拉取哪个任务的指针
    AtomicInteger offset;

    // 观察者模式的观察管理者
    ObserverManager observerManager;
    private Long intervalTime;//请求间隔时间
    private int scheduleLimit; //单词拉取任务数
    public Long cycleScheduleConfigTime = 10000L;// 多长时间拉取一次任务配置信息
    Map<String, ScheduleConfigEntity> scheduleConfigMap; // 存储任务配置信息


    public WorkerLaunch() {
        this(TaskConstant.DEFAULT_TASK_LIST_LIMIT);
    }

    public WorkerLaunch(int scheduleLimit) {
        this.scheduleLimit = scheduleLimit;
        scheduleConfigMap = new ConcurrentHashMap<>();
        loadPool = Executors.newScheduledThreadPool(1);
        taskManager = new HTTPTaskManager();

        taskTypes = new ArrayList<Class>() {{
            add(Video.class);
        }};
        packageName = taskTypes.get(0).getPackage().getName();
        this.scheduleLimit = scheduleLimit;
        observerManager = new ObserverManager();
        // 向观察管理者注册观察者
        observerManager.registerEvent(new StageObserver());
        offset = new AtomicInteger(0);
        // 初始化，拉取任务配置信息
        LOGGER.info(WorkerConstant.SVC_NAME +
                    " init params, taskTypes={}, packageName={}, observerManager={}",
                    taskTypes.toString(),
                    packageName,
                    observerManager.toString()
                );
        init();
    }


    @Override
    public void init() {
        loadConfig();
        if (scheduleLimit != 0) {
            LOGGER.info((WorkerConstant.SVC_NAME + "init ScheduleLimit:{}"), scheduleLimit);
            concurrentRunTimes = scheduleLimit;
            MaxConcurrentRunTimes = scheduleLimit;
        } else {
            scheduleLimit = scheduleConfigMap.get(taskTypes.get(0).getSimpleName()).getScheduleLimit();
        }
        // 定期更新任务配置信息
        loadPool.scheduleAtFixedRate(this::loadConfig, cycleScheduleConfigTime, cycleScheduleConfigTime, TimeUnit.MILLISECONDS);
    }

    //任务调度配置缓存到map中
    private void loadConfig() {
        LOGGER.info(WorkerConstant.SVC_NAME +
                    " init, loading schedule config...");
        //todo verify response
        List<ScheduleConfigEntity> configList = taskManager.getConfigList().getData();
        ScheduleConfigEntity scheduleConfigEntity = configList.get(0);
        System.out.println(scheduleConfigEntity);
        for (ScheduleConfigEntity config : configList) {
            scheduleConfigMap.put(config.getTaskType(), config);
        }
        LOGGER.info(WorkerConstant.SVC_NAME + " init," +
                " loading schedule config successfully! configList = {}",
                JSON.toJSONString(configList)
        );
    }

    @Override
    public int start() {
        LOGGER.info(WorkerConstant.SVC_NAME + " start...");
        offset.set(offset.get() == taskTypes.size() - 1 ? 0 : offset.incrementAndGet());
        Class taskType = taskTypes.get(offset.get());
        // 读取对应任务配置信息
        ScheduleConfigEntity scheduleConfig = scheduleConfigMap.get(taskType.getSimpleName());
        if (scheduleConfig == null) {
            LOGGER.error(ResponseStatus.ERR_GET_SCHEDULE_CONFIG.getMsg() + taskType.getSimpleName());
            throw new RuntimeException(ResponseStatus.ERR_GET_SCHEDULE_CONFIG.getMsg());
        }
        // 如果用户没有配置时间间隔就使用默认时间间隔
        intervalTime = scheduleConfig.getScheduleInterval() == 0 ? TaskConstant.DEFAULT_TIME_INTERVAL * 1000L : scheduleConfig.getScheduleInterval() * 1000L;

        this.threadPoolExecutor = new ThreadPoolExecutor(
                concurrentRunTimes,
                MaxConcurrentRunTimes,
                intervalTime + 1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(TaskConstant.DEFAULT_QUEUE_SIZE)
        );

        for (; ; ) {
            //单次拉取任务数放得下阻塞队列,就不断拉取执行
            if (TaskConstant.DEFAULT_QUEUE_SIZE - threadPoolExecutor.getQueue().size() >= scheduleLimit) {
                execute(taskType);
            }
            //任务队列满了,等待下一个周期
            try {
                Thread.sleep(intervalTime + (int) (Math.random() * 500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
//        for (int i = 0; i < concurrentRunTimes; i++) {
//            // 前后波动500ms
//            int step = (int) (Math.random() * 500 + 1);
//            // 拉取任务
//            threadPoolExecutor.scheduleAtFixedRate(this::execute, step * 3L, intervalTime + step, TimeUnit.MILLISECONDS);
//        }
    }

    public void execute(Class<?> taskType) {
        List<TaskBase> taskBaseList = scheduleTask(taskType);
        if (Utils.isNull(taskBaseList)) {
            LOGGER.warn(ResponseStatus.WARN_NO_TASK + taskType.getSimpleName());
            return;
        }
        int size = taskBaseList.size();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() ->
                    executeTask(taskBaseList, finalI)
            );
        }
    }


    // 拉取任务
    private List<TaskBase> scheduleTask(Class<?> taskType) {
        try {
            // 开始执行时，做点事，这里就是简单的打印了一句话，供后续扩展使用
            observerManager.wakeUpObserver(StageType.onBoot);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // 调用拉取任务接口拉取任务
        return getTaskBaseList(observerManager, taskType);
    }


    // 执行任务
    private void executeTask(List<TaskBase> TaskBaseList, int i) {
        TaskBase taskBase = TaskBaseList.get(i);
        try {
            // 执行前干点事，这里就打印了一句话，后续可以扩展
            observerManager.wakeUpObserver(StageType.onExecute, taskBase);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        TaskStageBase taskStageBase = null;
        Class<?> clazz = null;
        try {
            // 反射执行自定义任务
            clazz = getclazz(taskBase.getTaskType());
            Method method = TaskBuilder.getMethod(clazz,
                    taskBase.getTaskStage(),
                    taskBase.getTaskContext().getParams(),
                    taskBase.getTaskContext().getClazz()
            );
            LOGGER.info("开始执行, task={}, stage={}, taskBase={},",
                    clazz.getSimpleName(),
                    method.getName(),
                    taskBase
            );
            TaskRet returnVal = (TaskRet) method.invoke(
                    clazz.newInstance(),
                    taskBase.getTaskContext().getParams()
            );
            if (returnVal != null) {
                taskStageBase = returnVal.getTaskStageBase();
                Object result = returnVal.getResult();
                LOGGER.info("执行结果为：" + result);
            }
        } catch (Exception e) {
            try {
                //
                observerManager.wakeUpObserver(
                        StageType.onError,
                        taskBase,
                        scheduleConfigMap.get(taskBase.getTaskType()),
                        TaskBaseList,
                        clazz,
                        e
                );
                return;
            } catch (Exception exception) {
                LOGGER.error("wake up on error failed ", exception);
            }
        }
        try {
            // 任务成功后,观察者会刷新状态到数据库,可拓展其他观察者
            observerManager.wakeUpObserver(StageType.onFinish, taskBase, taskStageBase, clazz);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("wake up on finish failed ", e);
        }
    }

    public Class<?> getclazz(String taskType) throws ClassNotFoundException {
        return Class.forName(packageName + "." + taskType);
    }

    private List<TaskBase> getTaskBaseList(ObserverManager observerManager, Class<?> taskType) {
        LOGGER.info(WorkerConstant.SVC_NAME + "start pull task, taskType={}", taskType.getSimpleName());
        List<TaskResp> taskList;
        try {
            //todo verify response
            taskList = taskManager.getTaskList(new TaskFilterDTO(
                    taskType.getSimpleName(),
                    TaskStatus.PENDING.getStatus(),
                    scheduleConfigMap.get(taskType.getSimpleName()).getScheduleLimit()
            )).getData();
            if (Utils.isNull(taskList)) {
                LOGGER.warn(ResponseStatus.WARN_NO_TASK.getMsg());
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("error getTaskBaseList,observerManager={},taskType={}",
                    JSON.toJSONString(observerManager),
                    JSON.toJSONString(taskType),
                    e
            );
            return null;
        }
        //通知占据任务
        try {
            List<TaskBase> taskBaseList = new ArrayList<>();
            observerManager.wakeUpObserver(StageType.onObtain, taskList, taskBaseList);
            return taskBaseList;
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("error wake up obtain, taskList={},",
                    JSON.toJSONString(taskList),
                    e
            );
        }

        return null;
    }


    @Override
    public int destroy() {
        return 0;
    }
}
