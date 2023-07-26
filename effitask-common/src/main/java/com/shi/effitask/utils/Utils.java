package com.shi.effitask.utils;


import java.util.List;

public class Utils {
    /**
     * 获得任务Id
     * @return
     */
    public static String getTaskId() {
        return SnowFlake.nextId() + "";
    }

    public static boolean isNotNull(String s) {
        return s != null && !"".equals(s);
    }

    public static boolean isNull(String s) {
        return s == null || "".equals(s);
    }

    public static boolean isNull(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotNull(List list) {
        return list != null && !list.isEmpty();
    }

}
