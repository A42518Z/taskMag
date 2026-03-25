package com.taskmag.server.dataoperation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicQueryMapper {
    List<Map<String, Object>> selectPage(@Param("tableName") String tableName,
                                         @Param("selectColumns") String selectColumns,
                                         @Param("whereClause") String whereClause,
                                         @Param("params") Map<String, Object> params,
                                         @Param("offset") long offset,
                                         @Param("pageSize") int pageSize);

    Long count(@Param("tableName") String tableName,
               @Param("whereClause") String whereClause,
               @Param("params") Map<String, Object> params);
}
