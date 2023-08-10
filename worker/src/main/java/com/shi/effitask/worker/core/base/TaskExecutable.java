package com.shi.effitask.worker.core.base;

import com.shi.effitask.enums.TaskStatus;
import com.shi.effitask.worker.core.TaskBuilder;

import java.lang.reflect.Method;

public interface TaskExecutable<T> {

    TaskRet<T> handleProcess();
    TaskRet<T> handleFinish();
    TaskRet<T> handleError();
    TaskRet<T> contextLoad(String context);
    TaskRet<T> logLoad(String log);

    default TaskStageBase setStage(Class<?> clazz,
                                   String methodName,
                                   Object[] params,
                                   Class<?>[] parameterTypes,
                                   Object... envs) {
        //更新任务阶段
        return build(clazz, methodName, params, parameterTypes, envs);
    }


    // 利用类信息创建任务
    default TaskStageBase build(Class<?> clazz,
                                String methodName,
                                Object[] params,
                                Class<?>[] parameterTypes,
                                Object... envs) {
        TaskBuilder.checkParamsNum(params, parameterTypes);
        Method method = TaskBuilder.getMethod(clazz, methodName, params, parameterTypes);

        // get 方法名
        String taskStage = method.getName();

        // 上下文信息
        ScheduleContextBase taskContext = new ScheduleContextBase(params, envs, parameterTypes);
        return TaskStageBase.builder()
                .status(TaskStatus.PENDING.getStatus())
                .taskContext(taskContext)
                .taskStage(taskStage)
                .build();
    }

    default boolean judgeParamsTypes(Method clazzMethod, Class<?>[] parameterTypes) {
        Class<?>[] types = clazzMethod.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] != parameterTypes[i]) {
                return false;
            }
        }
        return true;
    }


}
