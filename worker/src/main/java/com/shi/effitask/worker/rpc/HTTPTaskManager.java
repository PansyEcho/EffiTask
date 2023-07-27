package com.shi.effitask.worker.rpc;

import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;

import java.util.List;

public class HTTPTaskManager implements TaskManager{
    @Override
    public String createTask(CreateTaskReq createTaskReq) {
        return null;
    }

    @Override
    public int updateTask(UpdateTaskReq updateTaskReq) {
        return 0;
    }

    @Override
    public TaskResp getTaskById(String taskId) {
        return null;
    }

    @Override
    public List<TaskResp> getTaskList(TaskFilterDTO filterDTO) {
        return null;
    }

    @Override
    public List<ScheduleConfigEntity> getConfigList() {
        return null;
    }

    @Override
    public List<TaskResp> getUserTaskList(QueryUserTaskReq queryUserTaskReq) {
        return null;
    }

    @Override
    public int saveConfig(ScheduleConfigEntity scheduleConfig) {
        return 0;
    }
}
