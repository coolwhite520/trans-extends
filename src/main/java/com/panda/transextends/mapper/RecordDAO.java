package com.panda.transextends.mapper;

import com.panda.transextends.pojo.Record;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RecordDAO {
    @Update("UPDATE translate_db.tbl_record SET Progress=#{percent} WHERE Id=#{id}")
    void updateProgress(@Param("id") long id, @Param("percent")  int percent);

    @Select("SELECT TransType FROM translate_db.tbl_record WHERE Id=#{id}")
    int queryTransTypeByRowId(@Param("id") long id);

    @Select("SELECT * FROM translate_db.tbl_record WHERE Id=#{id}")
    Record queryRecordById(@Param("id") long id);
}
