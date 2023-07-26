package com.shi.effitask.controller;


import com.shi.effitask.enums.ResponseStatus;
import com.shi.effitask.pojo.dto.Result;
import com.shi.effitask.pojo.entity.ScheduleConfigEntity;
import com.shi.effitask.service.IScheduleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/schedule/config")
public class ScheduleConfigController {

    Logger LOGGER = LoggerFactory.getLogger(ScheduleConfigController.class);

    @Resource
    private IScheduleConfigService scheduleConfigService;

    @GetMapping("/list")
    public Result<List<ScheduleConfigEntity>> getConfigList() {
        return scheduleConfigService.getScheduleConfigList();
    }

    @GetMapping("/save")
    public Result<Integer> saveConfig(@RequestBody ScheduleConfigEntity scheduleConfig) {
        if (!scheduleConfig.valid()) {
            LOGGER.error(ResponseStatus.ERR_INPUT_INVALID.getMsg());
            return Result.error(ResponseStatus.ERR_INPUT_INVALID);
        }
        return scheduleConfigService.save(scheduleConfig);
    }

}
