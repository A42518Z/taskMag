package com.taskmag.server.dataoperation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface DynamicCrudMapper {
    int insert(@Param("tableName") String tableName, @Param("data") Map<String, Object> data);

    int updateById(@Param("tableName") String tableName,
                   @Param("pkColumn") String pkColumn,
                   @Param("idValue") Object idValue,
                   @Param("data") Map<String, Object> data);

    int deleteById(@Param("tableName") String tableName,
                   @Param("pkColumn") String pkColumn,
                   @Param("idValue") Object idValue);
}
