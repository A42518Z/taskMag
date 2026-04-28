package com.taskmag.server.dataoperation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemFieldMapper {
    Map<String, Object> findTableMeta(@Param("dbName") String dbName,
                                      @Param("tableName") String tableName);

    List<String> findFieldNames(@Param("tableId") String tableId,
                                @Param("tableName") String tableName);

    Integer countLogicalPrimaryKeys(@Param("tableId") String tableId,
                                    @Param("tableName") String tableName);

    Integer countPhysicalPrimaryKeys(@Param("dbName") String dbName,
                                     @Param("tableName") String tableName);

    void addColumn(@Param("tableName") String tableName,
                   @Param("columnSql") String columnSql);

    void insertFieldMeta(@Param("rowid") String rowid,
                         @Param("tblid") String tblid,
                         @Param("tblname") String tblname,
                         @Param("enname") String enname,
                         @Param("cnname") String cnname,
                         @Param("dataType") String dataType,
                         @Param("dataLen") String dataLen,
                         @Param("isPKey") Integer isPKey,
                         @Param("ordIdx") String ordIdx,
                         @Param("lingmaSysEnt") String lingmaSysEnt);

    void markHasDefaultFields(@Param("tableId") String tableId);
}
