package com.shi.effitask.worker.core.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ScheduleLogBase {

    ScheduleData lastData;
    List<ScheduleData> historyDatas;
    public ScheduleLogBase() {
        lastData = new ScheduleData();
        historyDatas = new ArrayList<>();
    }
}
