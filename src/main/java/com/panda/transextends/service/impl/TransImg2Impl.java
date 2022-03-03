package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.OcrApi;
import com.panda.transextends.utils.PluginsApi;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransImg2Impl implements TransFile {

    @Autowired
    OcrApi ocrApi;

    @Autowired
    PluginsApi pluginsApi;

    @Autowired
    TransDocxImpl transDocx;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        // 把图片转为可编辑的pdf
        String outputFile = ocrApi.ocrmyimg(srcLang, srcFile);
        if (outputFile.equals("")) {
            return false;
        }
        srcFile = outputFile;
        // 转化为docx
        boolean p2dx = pluginsApi.convert(srcFile, "p2dx");
        if (p2dx) {
            String path = FilenameUtils.getFullPathNoEndSeparator(srcFile);
            String baseName = FilenameUtils.getBaseName(srcFile);
            String srcConFile = String.format("%s/%s.docx", path, baseName);
//                移除所有图片
            String rmPicFileName = transDocx.removeAllPictures(srcConFile);
            //翻译docx文档
            return transDocx.translate(rowId, srcLang, desLang, rmPicFileName, desFile);
        }
        return false;
    }
}
