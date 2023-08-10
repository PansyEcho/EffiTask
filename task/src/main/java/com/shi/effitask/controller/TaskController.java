package com.shi.effitask.controller;



import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;

import static com.shi.effitask.utils.Utils.isNull;

@RestController
@RequestMapping("/task")
public class TaskController {
    Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Resource
    private ITaskService taskService;

    /**
     * @param createTaskReq 创建任务请求体
     * @return taskId
     */

    @PostMapping("/create")
    public Result<String> createTask(@RequestBody CreateTaskReq createTaskReq) {
        if (!createTaskReq.valid()){
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.createTask(createTaskReq);
    }

    /**
     * @param taskId 任务ID
     * @return 根据任务ID获取任务DTO
     */

    @GetMapping("/get")
    public Result<TaskResp> getTask(@RequestParam("taskId") String taskId) {
        if (isNull(taskId)){
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.getTaskById(taskId);
    }

    /**
     * @param taskFilterDTO 过滤参数
     * @return 获取任务列表
     */

    @PostMapping("/list")
    public Result<List<TaskResp>> getTaskList(@RequestBody TaskFilterDTO taskFilterDTO) {
        if (!taskFilterDTO.valid()) {
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.getTaskList(taskFilterDTO);
    }

    /**
     * @param taskFilterDTO 过滤参数
     * @return 占据成功的任务列表
     */

    @PostMapping("/hold")
    public Result<List<TaskResp>> holdTask(@RequestBody TaskFilterDTO taskFilterDTO) {
        if (!taskFilterDTO.valid()) {
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.holdTask(taskFilterDTO);
    }

    /**
     * 更新任务信息
     * @param updateTaskReq 更新数据
     * @return 更新结果
     */

    @PostMapping("/update")
    public<T> Result<T> updateTask(@RequestBody UpdateTaskReq updateTaskReq) {
        if (!updateTaskReq.valid()) {
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.updateTask(updateTaskReq);
    }


    /**
     * @param userTaskReq 七个球参数
     * @return 获取指定用户的任务列表
     */

    @PostMapping("/user/list")
    public Result<List<TaskResp>> getUserTaskList(@RequestBody QueryUserTaskReq userTaskReq) {
        if (!userTaskReq.valid()) {
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return taskService.getTaskByUserIdAndStatus(userTaskReq);
    }




}
