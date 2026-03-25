package com.taskmag.server.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataOperationResponse {
    private ResultBody Result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultBody {
        private Boolean allowAdd;
        private String lingma_sys_key;
        private DataBody data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataBody {
        private List<Map<String, Object>> Items;
        private Long Count;
    }
}
