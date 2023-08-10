package com.shi.effitask.worker.core.observer;

import com.shi.effitask.worker.enums.StageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class ObserverManager {
    //任务队列
    List<TaskStage> observerList;

    public ObserverManager() {
        observerList = new ArrayList<>();
    }

    // 添加观察者
    public void registerEvent(TaskStage observer) {
        observerList.add(observer);
    }

    // 通过发射找到对应的方法执行
    public void wakeUpObserver(StageType stage, Object... params)
            throws InvocationTargetException, IllegalAccessException {

        for (TaskStage observer : observerList) {
            for (Method method : observer.getClass().getMethods()) {
                if (method.getName().equals(stage.name())) {
                    method.invoke(observer, params);
                }
            }
        }

    }

}
