package com.shi.effitask.pojo.dto;

import com.shi.effitask.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    private String msg;
    private int code;
    private T data;

    public Result() {

    }
    public Result(ResponseStatus status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
    }


    public Result(T data) {
        this(ResponseStatus.SUCCESS);
        this.data = data;
    }


    public static <T> Result<T> succeed() {
        return new Result<>(ResponseStatus.SUCCESS);
    }


    public static <T> Result<T> succeed(T data) {
        Result<T> result = new Result<>(ResponseStatus.SUCCESS);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResponseStatus status) {
        Result<T> result = new Result<>();
        result.code = status.getCode();
        result.msg = status.getMsg();
        return result;
    }




}
