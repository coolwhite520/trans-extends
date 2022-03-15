package com.panda.transextends.mapper;

import com.panda.transextends.pojo.Record;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecordDAO {
    @Update("UPDATE translate_db.tbl_record SET Progress=#{percent} WHERE Id=#{id}")
    void updateProgress(@Param("id") long id, @Param("percent")  int percent);

    @Select("SELECT TransType FROM translate_db.tbl_record WHERE Id=#{id}")
    int queryTransTypeByRowId(@Param("id") long id);


    @Select("SELECT * FROM translate_db.tbl_record WHERE Id=#{id}")
    Record queryRecordById(@Param("id") long id);

    @Select(" SELECT * FROM translate_db.tbl_record where Sha1=#{sha1}")
    List<Record> queryRecordsBySha1(@Param("sha1") String sha1);

    @Update("UPDATE `translate_db`.`tbl_record` set State=#{state}, StateDescribe=#{stateDescribe} where Id=#{id}")
    void updateRecordState( @Param("id") long id, @Param("state") int state, @Param("stateDescribe") String stateDescribe);

    @Update("UPDATE `translate_db`.`tbl_record` set Error=#{error} where Id=#{id}")
    void updateRecordError( @Param("id") long id, @Param("error") String error);

    @Update("UPDATE `translate_db`.`tbl_record` set Sha1=#{sha1} where Id=#{id}")
    void updateRecordSha1(@Param("id") long id, @Param("sha1") String sha1);

    @Update("UPDATE `translate_db`.`tbl_record` set StartAt=#{startAt} where Id=#{id}")
    void updateRecordStartAt(@Param("id") long id, @Param("startAt") String startAt);

    @Update("UPDATE `translate_db`.`tbl_record` set EndAt=#{endAt} where Id=#{id}")
    void updateRecordEndAt(@Param("id") long id, @Param("endAt") String endAt);

    @Update("UPDATE `translate_db`.`tbl_record` set OutFileExt=#{outFileExt} where Id=#{id}")
    void updateRecordOutFileExt(@Param("id") long id, @Param("outFileExt") String outFileExt);
}
