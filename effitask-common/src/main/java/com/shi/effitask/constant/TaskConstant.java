package com.shi.effitask.constant;

public class TaskConstant {

    //最大拉取任务数
    public static int MAX_TASK_LIST_LIMIT =  1000;

    //默认拉取任务数
    public static int DEFAULT_TASK_LIST_LIMIT =  100;

    //默认任务状态
    public static int DEFAULT_TASK_STATUS = 0;

    //用户ID
    public final static String USER_ID = "shi";

    //用户ID
    public final static int DEFAULT_QUEUE_SIZE = 10000;

    //默认拉取间隔,单位秒
    public final static int DEFAULT_TIME_INTERVAL = 20;
    //序列化上下文
    public final static String TASK_METHOD_CONTEXT_LOAD = "contextLoad";
    //序列化日志
    public final static String TASK_METHOD_LOG_LOAD = "logLoad";

}
