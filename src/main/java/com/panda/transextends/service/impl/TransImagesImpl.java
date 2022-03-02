package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.pojo.OcrEntity;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.CoreApi;
import com.panda.transextends.utils.OcrApi;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;

@Service
public class TransImagesImpl implements TransFile {

    @Autowired
    OcrApi ocrApi;

    @Autowired
    RecordDAO recordDAO;

    @Autowired
    CoreApi coreApi;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        List<OcrEntity> list = ocrApi.extract(srcLang, srcFile);
        long total = list.size();
        long current = 0;
        int percent = 0;
        XWPFDocument xwpfDocument = new XWPFDocument();
        for (OcrEntity ocrEntity : list) {
            String s = ocrEntity.getContent();
            XWPFParagraph paragraph = xwpfDocument.createParagraph();
            XWPFRun run = paragraph.createRun();
            if (StrUtil.isBlank(s))
                run.setText(s, 0);
            else {
                String transContent = coreApi.translate(srcLang, desLang, s);
                run.setText(transContent, 0);
                current++;
                if (percent != 100 * current / total) {
                    percent = (int) (100 * current / total);
                    if (percent > 100) percent = 100;
                    recordDAO.updateProgress(rowId, percent);
                }
            }

        }
        FileOutputStream outStream = null;
        outStream = new FileOutputStream(desFile);
        xwpfDocument.write(outStream);
        xwpfDocument.close();
        outStream.close();
        return false;
    }
}
