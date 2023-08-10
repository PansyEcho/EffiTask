package com.shi.effitask.worker.user.custom;


import com.alibaba.fastjson.JSON;
import com.shi.effitask.worker.core.base.ScheduleContextBase;
import com.shi.effitask.worker.core.base.ScheduleLogBase;
import com.shi.effitask.worker.core.base.TaskExecutable;
import com.shi.effitask.worker.core.base.TaskRet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Video implements TaskExecutable {

    Logger LOGGER = LoggerFactory.getLogger(Video.class);

    @Override
    public TaskRet handleProcess() {
        LOGGER.info("start process...");

        return null;
    }

    @Override
    public TaskRet handleFinish() {
        LOGGER.error("video handle finish");
        return null;
    }

    @Override
    public TaskRet handleError() {
        LOGGER.error("video handle error");
        return null;
    }

    @Override
    public TaskRet<ScheduleContextBase> contextLoad(String context) {
        LOGGER.info("video logLoad context={}", context);
        return new TaskRet<>(JSON.parseObject(context, ScheduleContextBase.class));
    }

    @Override
    public TaskRet<ScheduleLogBase> logLoad(String log) {
        LOGGER.info("video logLoad log={}", log);
        return new TaskRet<>(JSON.parseObject(log, ScheduleLogBase.class));
    }
}
