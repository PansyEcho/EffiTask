package com.shi.effitask.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleConfigEntity {

    private String taskType;

    private Integer scheduleLimit;

    private Integer scheduleInterval;

    private Integer maxProcessingTime;

    private Integer maxRetryNum;

    private Integer retryInterval;

    private Long createTime;

    private Long modifyTime;

    public boolean valid() {
        //limit
        return false;
    }

}
