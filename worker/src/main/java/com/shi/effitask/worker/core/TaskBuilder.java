package com.shi.effitask.worker.core;

import com.alibaba.fastjson.JSON;
import com.shi.effitask.constant.TaskConstant;
import com.shi.effitask.pojo.dto.CreateTaskReq;
import com.shi.effitask.worker.core.base.ScheduleContextBase;
import com.shi.effitask.worker.core.base.ScheduleLogBase;
import com.shi.effitask.worker.core.base.TaskExecutable;

import java.lang.reflect.Method;

public class TaskBuilder {


    public static CreateTaskReq build(TaskExecutable executable) throws NoSuchMethodException {
        Class<? extends TaskExecutable> clazz = executable.getClass();
        Method handProcess = clazz.getMethod("handleProcess");
        return TaskBuilder.build(clazz, handProcess.getName(), new Object[0], new Class[0]);
    }

    // 利用类信息创建任务
    public static CreateTaskReq build(Class<?> clazz, String methodName, Object[] params, Class<?>[] parameterTypes, Object... envs) {
        if (!TaskExecutable.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("The task must be implemented TaskDefinition!");
        }
        checkParamsNum(params, parameterTypes);
        Method method = getMethod(clazz, methodName, params, parameterTypes);

        // 获取类名
        String taskType = method.getDeclaringClass().getSimpleName();
        // 方法名
        String taskStage = method.getName();
        // 调度日志
        ScheduleLogBase scheduleLogBase = new ScheduleLogBase();
        String scheduleLog = JSON.toJSONString(scheduleLogBase);

        // 上下文信息
        ScheduleContextBase taskContextBase = new ScheduleContextBase(params, envs, parameterTypes);
        String taskContext = JSON.toJSONString(taskContextBase);
        return new CreateTaskReq(
                TaskConstant.USER_ID,
                taskType,
                taskStage,
                scheduleLog,
                taskContext
        );
    }

    public static void checkParamsNum(Object[] params, Class<?>[] parameterTypes) {
        // 参数个数检验
        if (params.length != parameterTypes.length) {
            throw new RuntimeException("Parameters are invalid!");
        }
    }

    public static Method getMethod(Class<?> clazz,
                                   String methodName,
                                   Object[] params,
                                   Class<?>[] parameterTypes) {
        Method method = null;
        for (Method clazzMethod : clazz.getMethods()) {
            // 获取对应要执行的方法
            if (clazzMethod.getName().equals(methodName)
                    && clazzMethod.getParameterCount() == params.length
                    && judgeParamsTypes(clazzMethod, parameterTypes)) {
                method = clazzMethod;
            }
        }

        return method;
    }


    private static boolean judgeParamsTypes(Method clazzMethod, Class<?>[] parameterTypes) {
        Class<?>[] types = clazzMethod.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] != parameterTypes[i]) {
                return false;
            }
        }
        return true;
    }
}
