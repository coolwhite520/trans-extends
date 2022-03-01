package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransTikaImpl implements TransFile {

    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        try {
            File file = new File(srcFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            Tika tika = new Tika();
            String content = tika.parseToString(fileInputStream);
            ArrayList<List<String>> list = coreApi.tokenize(srcLang, content);
            long total = list.size();
            long current = 0;
            int percent = 0;
            XWPFDocument xwpfDocument = new XWPFDocument();
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
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(desFile);
            xwpfDocument.write(outStream);
            xwpfDocument.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
