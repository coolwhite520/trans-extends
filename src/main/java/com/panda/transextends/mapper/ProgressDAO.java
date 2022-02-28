package com.panda.transextends.mapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProgressDAO {
    @Update("UPDATE translate_db.tbl_record SET Progress=#{percent} WHERE Id=#{rowId}")
    void updateProgress(@Param("rowId") int rowId, @Param("percent")  int percent);
}
