package com.taskmag.server.dataoperation.mapper;

import com.taskmag.server.dataoperation.dto.ColumnMetaDto;
import com.taskmag.server.dataoperation.dto.TableEntryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchemaMetaMapper {
    String currentDatabase();

    List<TableEntryDto> listTables(@Param("dbName") String dbName);

    List<ColumnMetaDto> listColumns(@Param("dbName") String dbName,
                                    @Param("tableName") String tableName);
}
