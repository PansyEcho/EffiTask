package com.shi.effitask.worker.core.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskStageBase {

    private String taskStage; //NOT NULL DEFAULT '', 存储任务阶段信息

    private int status; //tinyint(3) unsigned NOT NULL DEFAULT '0',

    private ScheduleContextBase taskContext;// varchar(8192) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务上下文，用户自定义',


}
