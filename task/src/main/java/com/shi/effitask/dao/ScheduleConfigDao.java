package com.shi.effitask.dao;

import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScheduleConfigDao {

    /**
     * 根据任务类型获取任务配置
     * @param taskType
     * @return
     */
    ScheduleConfigEntity getConfigByType(String taskType);

    /**
     * 新增
     * @param scheduleConfig
     */
    int save(ScheduleConfigEntity scheduleConfig);

    /**
     * 获取所有任务配置列表
     * @return
     */

    List<ScheduleConfigEntity> getScheduleConfigList();

}
