package com.panda.transextends.service;

import com.panda.transextends.constant.Constant;
import com.panda.transextends.factory.TransFactory;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.pojo.Record;
import com.panda.transextends.pojo.ReqBody;
import com.panda.transextends.utils.BaseResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TransFileService {
    @Autowired
    TransFactory transFactory;

    @Autowired
    RecordDAO recordDAO;

    public BaseResponse TransFile(ReqBody reqBody) {
        try {
            Record record = recordDAO.queryRecordById(reqBody.getId());
            final String srcFile = String.format("%s/%d/%s/%s%s",
                    Constant.UploadDir,
                    record.getUserId(),
                    record.getDirRandId(),
                    record.getFileName(),
                    record.getFileExt());
            File file = new File(srcFile);
            if(!file.exists()){
                String format = String.format("在服务器找不到文件 => %s", srcFile);
                return BaseResponse.error(format);
            }

            final String desFile = String.format("%s/%d/%s/%s%s",
                    Constant.OutputDir,
                    record.getUserId(),
                    record.getDirRandId(),
                    record.getFileName(),
                    record.getOutFileExt());
            int transType = record.getTransType();
            String fe = FilenameUtils.getExtension(srcFile);
            boolean success = transFactory.create(transType, fe).translate(record.getId(),
                    record.getSrcLang(),
                    record.getDesLang(),
                    srcFile,
                    desFile);
            if (!success) {
                return BaseResponse.error("翻译失败");
            }
            return BaseResponse.ok();
        }catch (Exception e) {
            return BaseResponse.error(e.toString());
        }
    }
}
