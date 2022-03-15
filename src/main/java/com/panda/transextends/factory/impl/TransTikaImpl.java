package com.panda.transextends.factory.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.factory.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransTikaImpl implements TransFile {

    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    @Override
    public boolean translate(long rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        File file = new File(srcFile);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream outStream = new FileOutputStream(desFile);
             XWPFDocument xwpfDocument = new XWPFDocument()) {
            Tika tika = new Tika();
            String content = tika.parseToString(fileInputStream);
            ArrayList<List<String>> list = coreApi.tokenize(srcLang, content);
            long total = list.size();
            long current = 0;
            int percent = 0;

            for (List<String> strings : list) {
                XWPFParagraph paragraph = xwpfDocument.createParagraph();
                for (String s : strings) {
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
            }
            xwpfDocument.write(outStream);
            return true;
        }
    }
}
