package com.shi.effitask.worker.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleContextBase {

    private Object[] params;

    private Object[] envs;

    private Class<?>[] clazz;

}
