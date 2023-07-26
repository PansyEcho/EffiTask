package com.shi.effitask.service.impl;

import com.shi.effitask.constant.TaskConstant;
import com.shi.effitask.dao.ScheduleConfigDao;
import com.shi.effitask.dao.SchedulePosDao;
import com.shi.effitask.dao.TaskDao;
import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.enums.TaskStatus;
import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.pojo.entity.SchedulePosEntity;
import com.shi.effitask.pojo.entity.TaskEntity;
import com.shi.effitask.service.ITaskService;
import com.shi.effitask.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements ITaskService {

    Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Resource
    private SchedulePosDao schedulePosDao;

    @Resource
    private ScheduleConfigDao scheduleConfigDao;

    @Resource
    private TaskDao taskDao;

    @Override
    public Result<String> createTask(CreateTaskReq createTaskReq) {
        SchedulePosEntity taskPos;
        try {
            taskPos = schedulePosDao.getTaskPos(createTaskReq.getTaskType());
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_POS.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_POS);
        }
        if (taskPos == null) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_POS.getMsg());
            return Result.error(ResponseStatus.ERR_GET_TASK_POS);
        }

        String tableName = getTableName(taskPos.getScheduleEndPos(), createTaskReq.getTaskType());
        ScheduleConfigEntity scheduleConfig;
        try {
            scheduleConfig = scheduleConfigDao.getConfigByType(createTaskReq.getTaskType());
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB);
        }

        TaskEntity taskEntity = new TaskEntity();
        String taskId = getTaskId(createTaskReq.getTaskType(), taskPos.getScheduleEndPos(), tableName);
        try {
            fillTaskEntity(createTaskReq, taskEntity, taskId, scheduleConfig);
            taskDao.create(tableName, taskEntity);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_CREATE_TASK.getMsg(), e);
            return Result.error(ResponseStatus.ERR_CREATE_TASK);
        }
        return Result.succeed(taskId);
    }


    @Override
    public Result<List<TaskResp>> getTaskList(TaskFilterDTO taskFilterDTO) {
        SchedulePosEntity schedulePos;
        try {
            schedulePos = schedulePosDao.getTaskPos(taskFilterDTO.getTaskType());
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB);
        }
        if (schedulePos == null || schedulePos.getScheduleBeginPos() == null) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB.getMsg());
            return Result.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB);
        }
        String tableName = getTableName(schedulePos.getScheduleBeginPos(), taskFilterDTO.getTaskType());
        List<TaskEntity> taskList;
        try {
            taskList = taskDao.getTaskList(taskFilterDTO.getTaskType(), taskFilterDTO.getStatus(), taskFilterDTO.getLimit(), tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB);
        }
        if (!Utils.isNotNull(taskList)) {
            return null;
        }
        List<TaskResp> taskReturns = getTaskReturns(taskList);
        return Result.succeed(taskReturns);
    }

    @Override
    public Result<List<TaskResp>> holdTask(TaskFilterDTO taskFilterDTO) {
        SchedulePosEntity schedulePos;
        try {
            schedulePos = schedulePosDao.getTaskPos(taskFilterDTO.getTaskType());
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB);
        }
        if (schedulePos == null || schedulePos.getScheduleBeginPos() == null) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB.getMsg());
            return Result.error(ResponseStatus.ERR_GET_TASK_SET_POS_FROM_DB);
        }
        String tableName = getTableName(schedulePos.getScheduleBeginPos(), taskFilterDTO.getTaskType());
        List<TaskEntity> taskList;
        try {
            taskList = taskDao.getTaskList(taskFilterDTO.getTaskType(), taskFilterDTO.getStatus(), taskFilterDTO.getLimit(), tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB);
        }
        if (!Utils.isNotNull(taskList)) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB.getMsg());
            return Result.error(ResponseStatus.ERR_GET_TASK_LIST_FROM_DB);
        }
        List<TaskEntity> filterList = taskList
                .stream()
                .parallel()
                .filter(task -> task.getCurrentRetryNum() == 0 || task.getMaxRetryInterval() != 0
                        && task.getOrderTime() <= System.currentTimeMillis())
                .collect(Collectors.toList());
        List<String> idList = filterList.stream().map(TaskEntity::getId).collect(Collectors.toList());
        if (!idList.isEmpty()) {
            taskDao.updateStatusBatch(idList, TaskStatus.EXECUTING.getStatus(), System.currentTimeMillis(), tableName);
        }
        List<TaskResp> taskRespList = getTaskReturnList(filterList);
        return Result.succeed(taskRespList);
    }

    private List<TaskResp> getTaskReturns(List<TaskEntity> taskList) {
        List<TaskResp> taskReturns = new ArrayList<>();
        for (TaskEntity taskEntity : taskList) {
            TaskResp taskResp = new TaskResp();
            BeanUtils.copyProperties(taskEntity, taskResp);
            taskReturns.add(taskResp);
        }
        return taskReturns;
    }

    private List<TaskResp> getTaskReturnList(List<TaskEntity> taskList) {
        List<TaskResp> taskRespList = new ArrayList<>();

        for (TaskEntity taskEntity : taskList) {
            TaskResp taskResp = new TaskResp();
            BeanUtils.copyProperties(taskEntity, taskResp);
            taskRespList.add(taskResp);
        }

        return taskRespList;
    }

    @Override
    public <T> Result<T> setTask(UpdateTaskReq updateTaskReq) {
        TaskEntity taskEntity;
        String tableName = getTableNameById(updateTaskReq.getTaskId());
        try {
            taskEntity = taskDao.find(updateTaskReq.getTaskId(), tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_INFO.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_INFO);
        }

        if (taskEntity == null) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_INFO.getMsg());
            return Result.error(ResponseStatus.ERR_GET_TASK_INFO);
        }
        taskEntity = buildUpdateTask(taskEntity, updateTaskReq);
        try {
            List<Integer> list = new ArrayList<Integer>() {{
                add(TaskStatus.SUCCESS.getStatus());
                add(TaskStatus.FAIL.getStatus());
            }};
            taskDao.updateTask(taskEntity, list, tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_SET_TASK.getMsg(), e);
            return Result.error(ResponseStatus.ERR_SET_TASK);
        }
        return Result.succeed();
    }

    @Override
    public Result<TaskResp> getTaskById(String task_id) {
        TaskEntity taskEntity;
        String tableName = getTableNameById(task_id);
        try {
            taskEntity = taskDao.find(task_id, tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_INFO.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_INFO);
        }
        TaskResp taskResp = new TaskResp();
        BeanUtils.copyProperties(taskEntity, taskResp);
        return Result.succeed(taskResp);
    }

    @Override
    public Result<List<TaskResp>> getTaskByUserIdAndStatus(QueryUserTaskReq userTaskReq) {
        List<TaskEntity> taskEntityList;
        String tableName = getTableName(1, "LarkTask");
        try {
            taskEntityList = taskDao.getTaskByUser_idAndStatus(userTaskReq.getUser_id(), getStatusList(userTaskReq.getStatusList()), tableName);
        } catch (Exception e) {
            LOGGER.error(ResponseStatus.ERR_GET_TASK_INFO.getMsg(), e);
            return Result.error(ResponseStatus.ERR_GET_TASK_INFO);
        }

        return Result.succeed(getTaskReturns(taskEntityList));
    }

    private List<Integer> getStatusList(int status) {
        List<Integer> statusList = new ArrayList<>();
        while (status != 0) {
            int cur = status & -status;
            statusList.add(cur);
            status ^= cur;
        }
        return statusList;
    }


    private String getTableNameById(String taskId) {
        String[] strs = taskId.split("_");
        return getTableName(Integer.parseInt(strs[3]), strs[1]);
    }


    public void fillTaskEntity(CreateTaskReq createTaskReq, TaskEntity taskEntity, String taskId, ScheduleConfigEntity scheduleConfig) {
        taskEntity.setTaskId(taskId);
        long currentTime = System.currentTimeMillis();
        taskEntity.setOrderTime(currentTime);
        BeanUtils.copyProperties(createTaskReq, taskEntity);

//        taskEntity.setUserId(createTaskReq.getUserId());
//        taskEntity.setTaskType(createTaskReq.getTaskType());
//        taskEntity.setTaskStage(createTaskReq.getTaskStage());
//        taskEntity.setScheduleLog(createTaskReq.getScheduleLog());
//        taskEntity.setTaskContext(createTaskReq.getTaskContext());
        taskEntity.setMaxRetryInterval(scheduleConfig.getRetryInterval());
        taskEntity.setMaxRetryNum(scheduleConfig.getMaxRetryNum());
    }


    private TaskEntity buildUpdateTask(TaskEntity taskEntity, UpdateTaskReq updateTaskReq) {
        if (isDefaultStatus(updateTaskReq.getStatus())) {
            taskEntity.setStatus(updateTaskReq.getStatus());
        }
        if (Utils.isNotNull(updateTaskReq.getTaskStage())) {
            taskEntity.setTaskStage(updateTaskReq.getTaskStage());
        }
        if (Utils.isNotNull(updateTaskReq.getTaskContext())) {
            taskEntity.setTaskContext(updateTaskReq.getTaskContext());
        }
        if (Utils.isNotNull(updateTaskReq.getScheduleLog())) {
            taskEntity.setScheduleLog(updateTaskReq.getScheduleLog());
        }
        if (isDefaultStatus(updateTaskReq.getMaxRetryInterval())) {
            taskEntity.setCurrentRetryNum(updateTaskReq.getCurrentRetryNum());
        }
        if (isDefaultStatus(updateTaskReq.getMaxRetryInterval())) {
            taskEntity.setMaxRetryInterval(updateTaskReq.getMaxRetryInterval());
        }
        if (isDefaultStatus(updateTaskReq.getMaxRetryNum())) {
            taskEntity.setMaxRetryNum(updateTaskReq.getMaxRetryNum());
        }
        if (updateTaskReq.getOrderTime() != 0) {
            taskEntity.setOrderTime(updateTaskReq.getOrderTime());
        }
        if (isDefaultStatus(updateTaskReq.getPriority())) {
            taskEntity.setPriority(updateTaskReq.getPriority());
        }

        taskEntity.setModifyTime(System.currentTimeMillis());
        return null;
    }

    private boolean isDefaultStatus(int status) {
        return status != TaskConstant.DEFAULT_TASK_STATUS;
    }


    private String getTaskId(String taskType, int taskPos, String tableName) {
        return Utils.getTaskId() + "_" + taskType + "_" + tableName + "_" + taskPos;
    }

    private String getTableName(Integer pos, String taskType) {
        return taskType.toLowerCase() + "_" + this.tableName() + "_" + pos;
    }

    public String tableName() {
        return "task";
    }

}
