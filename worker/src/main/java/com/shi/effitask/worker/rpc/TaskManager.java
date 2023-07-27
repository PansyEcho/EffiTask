package com.shi.effitask.worker.rpc;

import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;

import java.util.List;

public interface TaskManager {
    //创建任务
    String createTask(CreateTaskReq createTaskReq);
    //更新任务
    int updateTask(UpdateTaskReq updateTaskReq);
    //根据任务ID查询任务
    TaskResp getTaskById(String taskId);
    //获取任务列表
    List<TaskResp> getTaskList(TaskFilterDTO filterDTO);
    //获取配置表
    List<ScheduleConfigEntity> getConfigList();
    //获取用户配置
    List<TaskResp> getUserTaskList(QueryUserTaskReq queryUserTaskReq);
    //创建配置
    int saveConfig(ScheduleConfigEntity scheduleConfig);

}
