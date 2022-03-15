package com.panda.transextends.service;

import com.panda.transextends.constant.TransStatus;
import com.panda.transextends.transfactory.TransFactory;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.pojo.Record;
import com.panda.transextends.pojo.ReqBody;
import com.panda.transextends.utils.BaseResponse;
import com.panda.transextends.utils.Md5Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransFileService {
    @Autowired
    TransFactory transFactory;

    @Autowired
    RecordDAO recordDAO;

    public BaseResponse TransFile(ReqBody reqBody) {
        long id = reqBody.getId();
        try {
            // 更新状态，开始翻译
            recordDAO.updateProgress(id, 0);
            recordDAO.updateRecordState(id, TransStatus.TransBeginTranslate.getCode(), TransStatus.TransBeginTranslate.getMessage());
            recordDAO.updateRecordStartAt(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Record record = recordDAO.queryRecordById(id);
            // 判断原始文件是否存在
            String uploadAbsPath = String.format("%s/uploads/%d/%s", reqBody.getDataAbsPath(), record.getUserId(), record.getDirRandId());
            String outputAbsPath = String.format("%s/outputs/%d/%s",reqBody.getDataAbsPath(), record.getUserId(), record.getDirRandId());
            final String srcFile = String.format("%s/%s%s", uploadAbsPath, record.getFileName(), record.getFileExt());
            File file = new File(srcFile);
            if(!file.exists()){
                String format = String.format("在服务器找不到源文件 => %s", srcFile);
                throw new Exception(format);
            }
            // 创建输出目录
            final File f = new File(outputAbsPath);
            if (!f.exists()) {
                if(!f.mkdirs()) {
                    String format = String.format("创建目录失败 => %s", outputAbsPath);
                    throw new Exception(format);
                }
            }
            String fileExt = record.getFileExt();
            String outFileExt = "";
            String desFile = "";
            // 计算sha1
            final String md5 = Md5Util.getMD5(new File(srcFile));
            final String sha1 = String.format("%s&%s&%s", md5, record.getSrcLang(), record.getDesLang());
            // 如果srcLang 和 desLang相同，需要特殊处理，图片和pdf仅仅需要ocr，其他类型的文件copy就行
            if (record.getSrcLang().equals(record.getDesLang())) {
                if (record.getTransType() == 1 || fileExt.equalsIgnoreCase(".pdf")) {
                    outFileExt = ".docx";
                } else {
                    // 直接复制文件
                    outFileExt = fileExt;
                    desFile = String.format("%s/%s%s", outputAbsPath, record.getFileName(), outFileExt);
                    FileUtils.copyFile(new File(srcFile), new File(desFile));
                    recordDAO.updateRecordOutFileExt(id, outFileExt);
                    recordDAO.updateRecordSha1(id, sha1);
                    recordDAO.updateProgress(id, 100);
                    recordDAO.updateRecordState(id, TransStatus.TransTranslateSuccess.getCode(), TransStatus.TransTranslateSuccess.getMessage());
                    recordDAO.updateRecordEndAt(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    return BaseResponse.ok();
                }
            } else {
                // 确定下来输出文件的扩展名
                if (record.getTransType() == 1) {
                    outFileExt = ".docx";
                } else {
                    //文档类别的文件仅仅保留格式的类型为docx 、pptx 、 xlsx 、 eml
                    if (fileExt.equalsIgnoreCase(".docx") || fileExt.equalsIgnoreCase(".doc")){
                        outFileExt = ".docx";
                    } else if (fileExt.equalsIgnoreCase(".pptx")){
                        outFileExt = ".pptx";
                    } else if (fileExt.equalsIgnoreCase(".ppt")) {
                        outFileExt = ".ppt";
                    } else if (fileExt.equalsIgnoreCase(".xlsx")) {
                        outFileExt = ".xlsx";
                    } else if (fileExt.equalsIgnoreCase(".xls")) {
                        outFileExt = ".xls";
                    } else if (fileExt.equalsIgnoreCase(".eml")) {
                        outFileExt = ".eml";
                    } else {
                        outFileExt = ".docx";
                    }
                }
            }
            recordDAO.updateRecordOutFileExt(id, outFileExt);
            desFile = String.format("%s/%s%s", outputAbsPath, record.getFileName(), outFileExt);
            // 是否存在相同的sha1
            final List<Record> records = recordDAO.queryRecordsBySha1(sha1);
            for (Record r : records) {
                final String existFile = String.format("%s/outputs/%d/%s/%s%s", reqBody.getDataAbsPath(), r.getUserId(), r.getDirRandId(), r.getFileName(), r.getOutFileExt());
                if (!new File(existFile).exists()) {
                    break;
                }
                FileUtils.copyFile(new File(existFile), new File(desFile));
                recordDAO.updateRecordSha1(id, sha1);
                recordDAO.updateProgress(id, 100);
                recordDAO.updateRecordState(id, TransStatus.TransTranslateSuccess.getCode(), TransStatus.TransTranslateSuccess.getMessage());
                recordDAO.updateRecordEndAt(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return BaseResponse.ok();
            }
            // 没有找到缓存的文件，那么就正常翻译文件
            int transType = record.getTransType();
            String fe = FilenameUtils.getExtension(srcFile);
            transFactory.create(transType, fe).translate(record.getId(),record.getSrcLang(), record.getDesLang(), srcFile, desFile);
            // 更新完成状态 同时写入sha1
            recordDAO.updateRecordSha1(id, sha1);
            recordDAO.updateProgress(id, 100);
            recordDAO.updateRecordState(id, TransStatus.TransTranslateSuccess.getCode(), TransStatus.TransTranslateSuccess.getMessage());
            recordDAO.updateRecordEndAt(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return BaseResponse.ok();
        }catch (Exception e) {
            // 更新错误信息状态
            recordDAO.updateRecordState(id, TransStatus.TransTranslateFailed.getCode(), TransStatus.TransTranslateFailed.getMessage());
            recordDAO.updateRecordError(id, e.toString());
            recordDAO.updateRecordEndAt(id, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return BaseResponse.error(e.toString());
        }
    }
}
