package com.shi.effitask.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFilterDTO {

    public String taskType;

    public int status;

    public int limit;

    public boolean valid() {
        //limit
        return false;
    }
}
