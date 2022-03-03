package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.OcrApi;
import com.panda.transextends.utils.PluginsApi;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

@Service
public class TransPDFImpl implements TransFile {

    @Autowired
    PluginsApi pluginsApi;

    @Autowired
    TransDocxImpl transDocx;

    @Autowired
    OcrApi ocrApi;

    private boolean isEditable(String srcFile) {
        try {
            File file = new File(srcFile);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            if (StrUtil.isNotBlank(text) && text.trim().length() > 0) return true;
            document.close();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        if (!isEditable(srcFile)) {
            // 如果是不可以编辑的那么就先转化为可编辑的
            String outputFile = ocrApi.ocrmypdf(srcLang, srcFile);
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

        } else {
            // 转化为docx
            boolean p2dx = pluginsApi.convert(srcFile, "p2dx");
            if (p2dx) {
                String path = FilenameUtils.getFullPathNoEndSeparator(srcFile);
                String baseName = FilenameUtils.getBaseName(srcFile);
                String srcConFile = String.format("%s/%s.docx", path, baseName);
              //翻译docx文档
                return transDocx.translate(rowId, srcLang, desLang, srcConFile, desFile);
            }
        }
        return false;
    }
}
