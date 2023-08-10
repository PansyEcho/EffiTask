package com.shi.effitask.worker.rpc;

import com.alibaba.fastjson.JSON;
import com.shi.effitask.pojo.dto.*;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.worker.constant.TaskManagerUrl;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@SuppressWarnings("unchecked")
public class HTTPTaskManager implements TaskManager{

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPTaskManager.class);

    private static final OkHttpClient client = new OkHttpClient();



    //创建任务
    @Override
    public Result<String> createTask(CreateTaskReq createTaskReq) {
        return post(TaskManagerUrl.CREATE_TASK, createTaskReq);
    }

    //更新任务
    @Override
    public Result<Integer> updateTask(UpdateTaskReq updateTaskReq) {
        return post(TaskManagerUrl.UPDATE_TASK, updateTaskReq);
    }

    //根据ID查询任务
    @Override
    public Result<TaskResp> getTaskById(String taskId) {
        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskId);
        String url = TaskManagerUrl.IPORT + TaskManagerUrl.GET_TASK + getParamStr(params);
        return get(url);
    }



    @Override
    public Result<List<TaskResp>> getTaskList(TaskFilterDTO filterDTO) {
        return post(TaskManagerUrl.GET_TASK_LIST, filterDTO);
    }



    @Override
    public Result<List<ScheduleConfigEntity>> getConfigList() {
        Result result = get(TaskManagerUrl.IPORT + TaskManagerUrl.GET_CFG_LIST);
        Object data = result.getData();
        List<ScheduleConfigEntity> res = JSON.parseArray(JSON.toJSONString(data), ScheduleConfigEntity.class);
        return Result.succeed(res);
    }



    @Override
    public Result<List<TaskResp>> getUserTaskList(QueryUserTaskReq queryUserTaskReq) {
        return post(TaskManagerUrl.GET_USER_TASK_LIST, queryUserTaskReq);
    }



    @Override
    public Result<Integer> saveConfig(ScheduleConfigEntity scheduleConfig) {
        return post(TaskManagerUrl.IPORT + TaskManagerUrl.CREATE_TASK_CFG, scheduleConfig);
    }


    // get方法
    @SuppressWarnings("ALL")
    public Result get(String url) {

        Request request = new Request.Builder().url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.error("fail http get " + url + ":" + JSON.toJSONString(response));
                return null;
            }
            if (response.body() != null){
                String result = Objects.requireNonNull(response.body()).string();
                return JSON.parseObject(result, Result.class);
            }
        } catch (Exception e) {
            LOGGER.error("fail http get " + url, e);
            e.printStackTrace();
        }
        return null;
    }

    // 拼接url参数
    private String getParamStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    @SuppressWarnings("ALL")
    // post请求
    public <E> Result post(String url, E body) {
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(TaskManagerUrl.IPORT + url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(body)))
                .build();

        String result;
        try {
            result = client.newCall(request).execute().body().string();
            return JSON.parseObject(result, Result.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
