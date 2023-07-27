package com.shi.effitask.worker.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskBase {

    private String userId; //NOT NULL DEFAULT '',

    private String taskId; // NOT NULL DEFAULT '',

    private String taskType; //NOT NULL DEFAULT '',

    private String taskStage; //NOT NULL DEFAULT '',

    private int status; //tinyint(3) unsigned NOT NULL DEFAULT '0',

    private int currentRetryNum; //NOT NULL DEFAULT '0' COMMENT '已经重试几次了',

    private int maxRetryNum; //NOT NULL DEFAULT '0' COMMENT '最大能重试几次',

    private long orderTime;//调度时间，越小越优先

    private int priority;//调度优先级

    private int maxRetryInterval;// int(11) NOT NULL DEFAULT '0' COMMENT '最大重试间隔',

    private ScheduleLogBase scheduleLog;// varchar(4096) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '调度信息记录',

    private ScheduleContextBase taskContext;// varchar(8192) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务上下文，用户自定义',

    private Long createTime;// datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,

    private Long modifyTime;// datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


}
