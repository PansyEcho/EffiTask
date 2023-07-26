package com.shi.effitask.enums;

public enum ResponseStatus {
    SUCCESS(0, "ok"),
    ERR_INPUT_INVALID(8020, "input invalid"),
    ERR_SHOULD_BIND(8021, "should bind failed"),
    ERR_JSON_MARSHAL(8022, "json marshal failed"),
    ERR_GET_TASK_INFO(8035, "get task info failed"),
    ERR_GET_TASK_HANDLE_PROCESS(8036, "get task handle process Failed"),
    ERR_CREATE_TASK(8037, "create task failed"),
    ERR_GET_TASK_LIST_FROM_DB(8038, "get task list from db failed"),
    ERR_GET_TASK_SET_POS_FROM_DB(8039, "get task set pos from db failed"),
    ERR_INCREASE_CRT_RETRY_NUM(8040, "set task failed"),
    ERR_SET_TASK(8041, "increase crt retry num failed"),
    ERR_GET_TASK_POS(8042, "get task pos failed"),
    ERR_GET_PROCESSING_COUNT(8043, "get processing count failed"),
    ERR_SET_USER_PRIORITY(8045, "set user priority failed"),
    ERR_GET_SCHEDULE_CONFIG(8046, "get schedule config failed"),
    ERR_SET_SCHEDULE_CONFIG(8047, "set schedule config failed");

    private int code;
    private String msg;
    private ResponseStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
