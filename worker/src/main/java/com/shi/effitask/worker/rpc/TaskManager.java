package com.shi.effitask.worker.rpc;

import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;

import java.util.List;

public interface TaskManager {
    //创建任务
    Result<String> createTask(CreateTaskReq createTaskReq);
    //更新任务
    Result<Integer> updateTask(UpdateTaskReq updateTaskReq);
    //根据任务ID查询任务
    Result<TaskResp> getTaskById(String taskId);
    //获取任务列表
    Result<List<TaskResp>> getTaskList(TaskFilterDTO filterDTO);
    //获取用户配置
    Result<List<TaskResp>> getUserTaskList(QueryUserTaskReq queryUserTaskReq);
    //获取配置表
    Result<List<ScheduleConfigEntity>> getConfigList();
    //创建配置
    Result<Integer> saveConfig(ScheduleConfigEntity scheduleConfig);

}
