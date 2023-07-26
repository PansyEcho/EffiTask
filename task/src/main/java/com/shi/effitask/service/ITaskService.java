package com.shi.effitask.service;

import com.shi.effitask.pojo.dto.*;

import java.util.List;

public interface ITaskService {

    /**
     *
     * @param createTaskReq 创建任务请求体
     * @return taskId 任务ID
     */
    Result<String> createTask(CreateTaskReq createTaskReq);

    /**
     * @param taskFilterDTO 过滤参数
     * @return 获取任务列表
     */
    Result<List<TaskResp>> getTaskList(TaskFilterDTO taskFilterDTO);


    /**
     * @param taskFilterDTO 过滤参数
     * @return 占据成功的任务列表
     */
    Result<List<TaskResp>> holdTask(TaskFilterDTO taskFilterDTO);

    /**
     * 更新任务信息
     * @param updateTaskReq 更新数据
     * @return 更新结果
     */
    <T> Result<T> setTask(UpdateTaskReq updateTaskReq);

    /**
     * @param task_id 任务ID
     * @return 获取任务
     */
    Result<TaskResp> getTaskById(String task_id);

    /**
     * @param userTaskReq 七个球参数
     * @return 获取指定用户的任务列表
     */
    Result<List<TaskResp>> getTaskByUserIdAndStatus(QueryUserTaskReq userTaskReq);



}
