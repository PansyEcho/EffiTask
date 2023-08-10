package com.shi.effitask.dao;


import com.shi.effitask.pojo.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskDao {

    /**
     * 创建任务
     * @param task
     * @param tableName
     */
    int create(@Param("tableName") String tableName, @Param("task") TaskEntity task);


    /**
     * 获得对应状态的对应任务列表
     * @param taskType 任务类型
     * @param status 任务状态
     * @param limit 限制数目
     * @param tableName
     * @return
     */
    List<TaskEntity> getTaskList(String taskType, int status, int limit, String tableName);

    /**
     * 更新任务信息
     * @param Task
     * @param status
     * @param tableName
     */
    void updateTask(@Param("task") TaskEntity Task, @Param("status") List<Integer> status, @Param("tableName") String tableName);

    /**
     * 获得活跃状态的任务
     * @param statusList 活跃状态列表
     * @param tableName
     * @return
     */
    List<TaskEntity> getAliveTaskList(List<Integer> statusList, String tableName);

    /**
     * 获取对应状态的任务数
     * @param status 任务状态
     * ≈
     * @return
     */
    int getTaskCountByStatus(int status, String tableName);

    /**
     * 获取任务状态列表中的任务数
     * @param statusList
     * @param tableName
     * @return
     */
    int getTaskCount(List<Integer> statusList, String tableName);

    /**
     * 获取处于执行状态的超过最大执行时间的任务列表
     * @param status 任务状态
     * @param limit 限制数目
     * @param maxProcessTime 任务最大执行时间
     * @param currentTime 当前时间
     * @param tableName
     * @return
     */
    List<TaskEntity> getLongTimeProcessing(int status, int limit, long maxProcessTime, long currentTime, String tableName);

    /**
     *  增加重试次数
     * @param taskId
     * @param tableName
     */
    void increaseCrtRetryNum(String taskId, String tableName);

    /**
     *  根据任务查找任务
     * @param taskId
     * @param tableName
     * @return
     */
    TaskEntity find(String taskId, String tableName);

    /**
     * 设置任务状态
     * @param taskId
     * @param tableName
     */
    void setStatus(String taskId, String tableName);

    /**
     * 更改任务上下文
     * @param taskId
     * @param tableName
     */
    void updateTask_contextByTask_id(String taskId, String tableName, String context);

    /**
     * 更改超时的任务为Pending状态
     * @param currentTime
     * @param maxProcessingTime
     * @param oldStatus
     * @param newStatus
     * @param tableName
     */
    void modifyTimeoutPending(Long currentTime, Long maxProcessingTime, int oldStatus, int newStatus, String tableName);

    /**
     * 查看指定用户的任务
     * @param userId
     * @param statusList
     * @param tableName
     * @return
     */
    List<TaskEntity> getTaskByUser_idAndStatus(String userId, List<Integer> statusList, String tableName);

    /**
     * 将列表中的任务修改为指定状态
     * @param ids
     * @param status
     * @param tableName
     * @return
     */
    int updateStatusBatch(List<String> ids, int status, long modifyTime, String tableName);

}
