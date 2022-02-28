package com.panda.transextends.service.impl;

import com.panda.transextends.mapper.ProgressDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.FormatConvert;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class TransDocImpl implements TransFile {

    public long calculateTotalProgress(String srcFile) {
        return 0;
    }

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile)  {
        HWPFDocument document = null;
        try {
            InputStream inputStream = new FileInputStream(srcFile);
            POIFSFileSystem pfs = new POIFSFileSystem(inputStream);
            document = new HWPFDocument(pfs);
            //得到文档的读取范围
            Range range = document.getRange();
            for (int i = 0; i < range.numParagraphs(); i++) {
                Paragraph p = range.getParagraph(i);
                System.out.println(p.text());
//                p.replaceText("abc", true);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (document != null) {
                    FileOutputStream outStream = null;
                    outStream = new FileOutputStream(desFile);
                    document.write(outStream);
                    document.close();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
