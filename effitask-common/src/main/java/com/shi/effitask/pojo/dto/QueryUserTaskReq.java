package com.shi.effitask.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserTaskReq {

    public String user_id;

    public int statusList;

    public boolean valid() {
        return false;
    }

}
