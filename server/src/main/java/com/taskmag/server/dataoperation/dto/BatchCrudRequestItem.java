package com.taskmag.server.dataoperation.dto;

import lombok.Data;

@Data
public class BatchCrudRequestItem {
    private String TableName;
    private CrudModelDto CrudModel;
}
