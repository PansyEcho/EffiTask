package com.shi.effitask.worker.core.observer;

import com.shi.effitask.constant.TaskConstant;
import com.shi.effitask.pojo.dto.TaskResp;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.worker.core.annotation.Stage;
import com.shi.effitask.worker.core.base.TaskBase;
import com.shi.effitask.worker.core.base.TaskStageBase;
import com.shi.effitask.worker.enums.StageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 框架本身针对各阶段的操作,比如保存上下文等,与业务逻辑剥离开的模块
 */
public class StageObserver implements TaskStage {

    Logger LOGGER = LoggerFactory.getLogger(StageObserver.class);

    private Long beginTime;



    @Override
    @Stage(stageType = StageType.onBoot)
    public void onBoot() {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onBoot.getMsg());
    }


    @Override
    public void onObtain(List<TaskResp> taskList, List<TaskBase> taskBaseList) {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onObtain.getMsg());

    }

    @Override
    public void onExecute(TaskBase asyncTaskReturn) {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onExecute.getMsg());
        this.beginTime = System.currentTimeMillis();
    }

    @Override
    public void onFinish(TaskBase asyncTaskReturn, TaskStageBase taskStage, Class<?> aClass) {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onFinish.getMsg());

    }

    @Override
    public void onError(TaskBase asyncTaskReturn, ScheduleConfigEntity scheduleConfig, List<TaskBase> TaskBaseList, Class<?> aClass, Exception e) {
        LOGGER.info("userId={}, thread={} on {}", TaskConstant.USER_ID, Thread.currentThread().getName(), StageType.onError.getMsg());

    }


}
