package com.shi.effitask.worker.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TaskRet<T> {

    T result;
    TaskStageBase taskStageBase;
    public TaskRet(T result) {
        this(result, null);
    }
    public TaskRet(T result, TaskStageBase taskStageBase) {
        this.result = result;
        this.taskStageBase = taskStageBase;
    }

}
