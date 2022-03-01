package com.panda.transextends.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface RecordDAO {
    @Update("UPDATE translate_db.tbl_record SET Progress=#{percent} WHERE Id=#{rowId}")
    void updateProgress(@Param("rowId") int rowId, @Param("percent")  int percent);

    @Select("SELECT TransType FROM translate_db.tbl_record WHERE Id=#{rowId}")
    int queryTransTypeByRowId(@Param("rowId") int rowId);
}
