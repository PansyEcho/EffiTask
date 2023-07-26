package com.shi.effitask.service;


import com.shi.effitask.pojo.dto.Result;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;

import java.util.List;

public interface IScheduleConfigService {

    /**
     * @return 获取任务配置
     */
    Result<List<ScheduleConfigEntity>> getScheduleConfigList();

    /**
     * @return 新增任务配置项
     */
    Result<Integer> save(ScheduleConfigEntity scheduleConfig);

}
