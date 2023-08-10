package com.shi.effitask.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulePosEntity {

    private Long id;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 调度开始于几号表
     */
    private Integer scheduleBeginPos;

    /**
     * 调度结束于几号表
     */
    private Integer scheduleEndPos;

    private Long createTime;

    private Long modifyTime;

}
