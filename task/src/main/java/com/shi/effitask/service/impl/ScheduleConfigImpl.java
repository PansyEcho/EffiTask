package com.shi.effitask.service.impl;

import com.shi.effitask.dao.ScheduleConfigDao;
import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.pojo.dto.Result;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.service.IScheduleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class ScheduleConfigImpl implements IScheduleConfigService {

    Logger LOGGER = LoggerFactory.getLogger(ScheduleConfigImpl.class);

    @Autowired
    private ScheduleConfigDao scheduleConfigDao;


    @Override
    public Result<List<ScheduleConfigEntity>> getScheduleConfigList() {
        List<ScheduleConfigEntity> taskTypeCfgList;
        try {
            taskTypeCfgList = scheduleConfigDao.getScheduleConfigList();
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_SCHEDULE_CONFIG.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_SCHEDULE_CONFIG);
        }
        return Result.succeed(taskTypeCfgList);
    }

    @Override
    public Result<Integer> save(ScheduleConfigEntity scheduleConfig) {
        int rowAffect = 0;
        try {
            rowAffect = scheduleConfigDao.save(scheduleConfig);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_SET_SCHEDULE_CONFIG.getMsg(), e);
            return Result.error(ResponseStatus.ERR_SET_SCHEDULE_CONFIG);
        }
        return Result.succeed(rowAffect);
    }

}
