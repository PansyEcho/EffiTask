package com.shi.effitask.worker.core.annotation;

import com.shi.effitask.worker.enums.StageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Stage {

    StageType stageType();


}
