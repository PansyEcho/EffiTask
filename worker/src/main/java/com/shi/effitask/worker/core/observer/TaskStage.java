package com.shi.effitask.worker.core.observer;

import com.shi.effitask.pojo.dto.TaskResp;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.worker.core.base.TaskBase;
import com.shi.effitask.worker.core.base.TaskStageBase;

import java.util.List;

public interface TaskStage {
    //启动加载
    void onBoot();
    //占据任务时
    void onObtain(List<TaskResp> taskList, List<TaskBase> taskBaseList);
    //执行任务前
    void onExecute(TaskBase asyncTaskReturn);
    //执行结束
    void onFinish(TaskBase asyncTaskReturn, TaskStageBase taskStage, Class<?> clazz);
    //执行失败
    void onError(TaskBase asyncTaskReturn, ScheduleConfigEntity scheduleConfig, List<TaskBase> TaskBaseList, Class<?> clazz, Exception e);

}
