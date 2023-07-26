package com.shi.effitask.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskReq {

    private String userId;

    private String taskType;

    private String taskStage;

    private String scheduleLog;

    private String taskContext;

    public boolean valid() {
        return true;
    }

}
