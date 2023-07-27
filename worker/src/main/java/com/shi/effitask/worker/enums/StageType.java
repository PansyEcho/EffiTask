package com.shi.effitask.worker.enums;

public enum StageType {

    onBoot(0, "boot"),
    onError(1, "error"),
    onExecute(2, "execute"),
    onFinish(3, "finish"),
    onObtain(4, "obtain");

    private final int code;
    private final String msg;

    private StageType(int code, String msg) {
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
