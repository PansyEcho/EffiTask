package com.shi.effitask.worker.core.observer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.shi.effitask.constant.TaskConstant;
import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.enums.TaskStatus;
import com.shi.effitask.pojo.dto.TaskResp;
import com.shi.effitask.pojo.dto.UpdateTaskReq;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.worker.boot.WorkerLaunch;
import com.shi.effitask.worker.core.TaskBuilder;
import com.shi.effitask.worker.core.annotation.Stage;
import com.shi.effitask.worker.core.base.*;
import com.shi.effitask.worker.enums.StageType;
import com.shi.effitask.worker.rpc.HTTPTaskManager;
import com.shi.effitask.worker.rpc.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 框架本身针对各阶段的操作,比如保存上下文等,与业务逻辑剥离开的模块
 */
public class StageObserver implements TaskStage {

    Logger LOGGER = LoggerFactory.getLogger(StageObserver.class);

    private Long beginTime;


    TaskManager taskManager = new HTTPTaskManager();


    @Override
    @Stage(stageType = StageType.onBoot)
    public void onBoot() {
        //任务启动,可拓展
        LOGGER.info("userId={}, thread={} on {}",
                TaskConstant.USER_ID,
                Thread.currentThread().getName(),
                StageType.onBoot.getMsg()
        );


    }


    @Override
    @Stage(stageType = StageType.onObtain)
    public void onObtain(List<TaskResp> taskList, List<TaskBase> taskBaseList) {
        //占据任务,获取到可占据的任务后taskList后,taskBaseList
        convertTask(taskList, taskBaseList);
        LOGGER.info("userId={}, thread={} on {}, taskList={}, taskBaseList={}",
                TaskConstant.USER_ID,
                Thread.currentThread().getName(),
                StageType.onObtain.getMsg(),
                JSON.toJSONString(taskList),
                JSON.toJSONString(taskBaseList)
        );
    }


    public Class<?> getclazz(String taskType) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(WorkerLaunch.packageName + "." + taskType);
        return clazz;
    }

    /**
     * 转化占据任务,序列化其上下文和调度日志
     */
    private List<TaskBase> convertTask(List<TaskResp> taskList, List<TaskBase> obtainedList) {

        for (TaskResp taskResp : taskList) {
            TaskBase taskBase = new TaskBase();
            BeanUtil.copyProperties(taskResp, taskBase);
            //序列化上下文
            if (loadContext(taskResp, taskBase)) { return null; }
            //序列化调度日志
            if (loadLog(taskResp, taskBase)) { return null; }
            obtainedList.add(taskBase);
        }

        return obtainedList;
    }

    private boolean loadLog(TaskResp taskResp, TaskBase taskBase) {
        TaskRet<ScheduleLogBase> logBaseRet = null;
        try {
            logBaseRet = invokeMethod(
                    getclazz(taskBase.getTaskType()),
                    TaskConstant.TASK_METHOD_LOG_LOAD,
                    new Object[]{taskResp.getScheduleLog()},
                    new Class[]{String.class}
            );
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_TRANSFORM_LOG.getMsg(), e);
        }
        if (logBaseRet != null) {
            taskBase.setScheduleLog(logBaseRet.getResult());
            return true;
        }
        return true;
    }

    private boolean loadContext(TaskResp taskResp, TaskBase taskBase) {
        TaskRet<ScheduleContextBase> contextBaseRet = null;
        try {
            contextBaseRet = invokeMethod(
                    getclazz(taskBase.getTaskType()),
                    TaskConstant.TASK_METHOD_CONTEXT_LOAD,
                    new Object[]{taskResp.getTaskContext()},
                    new Class[]{String.class}
            );
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_TRANSFORM_CONTEXT.getMsg(), e);
        }
        if (contextBaseRet != null) {
            taskBase.setTaskContext(contextBaseRet.getResult());
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> TaskRet<T> invokeMethod(Class<?> clazz,
                                        String methodName,
                                        Object[] params,
                                        Class<?>[] paramsType) {
        if (clazz == null) {
            LOGGER.error("class is null, methodName={},params={},paramsType={}",
                    methodName,
                    JSON.toJSONString(params),
                    JSON.toJSONString(paramsType)
            );
            return null;
        }
        TaskRet<T> returnVal = null;
        // 利用Java反射执行本地方法
        Method method = TaskBuilder.getMethod(clazz, methodName, params, paramsType);
        try {
            returnVal = (TaskRet<T>) method.invoke(clazz.newInstance(), params);
            if (returnVal != null) {
                Object result = returnVal.getResult();
                LOGGER.info("result={}, clazz={},methodName={},params={},paramsType={}",
                        JSON.toJSONString(result),
                        JSON.toJSONString(clazz),
                        methodName,
                        JSON.toJSONString(params),
                        JSON.toJSONString(paramsType)
                );
            }
        } catch (Exception e) {
            LOGGER.error("clazz={},methodName={},params={},paramsType={}",
                    JSON.toJSONString(clazz),
                    methodName,
                    JSON.toJSONString(params),
                    JSON.toJSONString(paramsType),
                    e
            );
        }
        return returnVal;
    }

    @Override
    @Stage(stageType = StageType.onExecute)
    public void onExecute(TaskBase taskBase) {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onExecute.getMsg());
        this.beginTime = System.currentTimeMillis();
    }

    @Override
    @Stage(stageType = StageType.onFinish)
    public void onFinish(TaskBase taskBase,
                         TaskStageBase taskStage,
                         Class<?> clazz) {
        LOGGER.info("userId={}, thread={} on {}",
                TaskConstant.USER_ID,
                Thread.currentThread().getName(),
                StageType.onFinish.getMsg()
        );
        //修改任务状态
        UpdateTaskReq updateTaskReq = modifyTaskInfo(
                taskBase,
                TaskStatus.SUCCESS,
                taskStage
        );
        long cost = System.currentTimeMillis() - beginTime;
        updateTaskReq.setScheduleLog(
                JSON.toJSONString(
                        getScheduleLog(
                                taskBase,
                                cost,
                                ""
                        )
                )
        );
        //所有阶段的stage都执行完毕
        if (taskStage == null) {
            invokeMethod(clazz, "handleFinish", new Object[0], new Class[0]);
        }
        //更新任务
        try {
            //todo verify response
            int rowEffect = taskManager.updateTask(updateTaskReq).getData();
            if (rowEffect == 0) {
                LOGGER.error("update task fail. updateReq={}, userId={}, thread={} on {}",
                        JSON.toJSONString(updateTaskReq),
                        TaskConstant.USER_ID,
                        Thread.currentThread().getName(),
                        StageType.onFinish.getMsg()
                );
            }
        }catch (Exception e) {
            LOGGER.error("update task fail. updateReq={}, userId={}, thread={} on {}",
                    JSON.toJSONString(updateTaskReq),
                    TaskConstant.USER_ID,
                    Thread.currentThread().getName(),
                    StageType.onFinish.getMsg(),
                    e
            );
        }
        LOGGER.info("update task succeed. taskType={}, userId={}, thread={} on {}",
                taskBase.getTaskType(),
                TaskConstant.USER_ID,
                Thread.currentThread().getName(),
                StageType.onFinish.getMsg()
        );
    }

    public String getScheduleLog(TaskBase taskBase, long costTime, String errMsg) {
        // 记录调度信息
        ScheduleLogBase scheduleLog = taskBase.getScheduleLog();
        ScheduleData lastData = scheduleLog.getLastData();
        List<ScheduleData> historyDataList = scheduleLog.getHistoryDatas();
        historyDataList.add(lastData);
        if (historyDataList.size() > 3) {
            historyDataList.remove(0);
        }
        ScheduleData scheduleData = new ScheduleData(
                UUID.randomUUID() + "", errMsg, costTime + ""
        );
        scheduleLog.setLastData(scheduleData);
        return JSON.toJSONString(scheduleLog);
    }


    // 修改任务状态
    public UpdateTaskReq modifyTaskInfo(TaskBase taskBase,
                                        TaskStatus taskStatus,
                                        TaskStageBase taskStageBase) {

        UpdateTaskReq updateTaskReq = UpdateTaskReq.builder().
                taskId(taskBase.getTaskId())
                .taskContext(taskStageBase != null ? JSON.toJSONString(taskStageBase.getTaskContext()) : JSON.toJSONString(taskBase.getTaskContext()))
                .priority(taskBase.getPriority())
                .taskStage(taskStageBase != null ? taskStageBase.getTaskStage() : taskBase.getTaskStage()).status(taskStatus.getStatus())
                .currentRetryNum(taskBase.getCurrentRetryNum())
                .maxRetryInterval(taskBase.getMaxRetryInterval())
                .orderTime(taskStageBase != null ? System.currentTimeMillis() - taskBase.getPriority() : taskBase.getOrderTime() - taskBase.getPriority())
                .build();
        updateTaskReq.setStatus(taskStageBase != null ? TaskStatus.PENDING.getStatus() : taskStatus.getStatus());
        return updateTaskReq;
    }


    // 任务执行失败,更改任务状态为PENDING，重试次数+1，超过重试次数设置为FAIL
    @Override
    @Stage(stageType = StageType.onError)
    public void onError(TaskBase taskBase,
                        ScheduleConfigEntity scheduleConfig,
                        List<TaskBase> TaskBaseList,
                        Class<?> clazz,
                        Exception e) {
        //简单打印
        LOGGER.info("userId={}, thread={} on {}",
                TaskConstant.USER_ID,
                Thread.currentThread().getName(),
                StageType.onError.getMsg()
        );

    }


}
