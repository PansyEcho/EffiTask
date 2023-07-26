package com.shi.effitask.dao;

import com.shi.effitask.pojo.entity.SchedulePosEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchedulePosDao {

    /**
     * 新增任务位置
     * @param scheduleEntity
     */
    int save(SchedulePosEntity scheduleEntity);

    /**
     * 更新任务位置
     * @param scheduleEntity
     */
    int update(SchedulePosEntity scheduleEntity);

    /**
     * 获取任务位置信息
     * @param task_type
     * @return
     */
    SchedulePosEntity getTaskPos(String task_type);



}
