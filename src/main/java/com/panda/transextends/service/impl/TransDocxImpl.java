package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.ProgressDAO;
import com.panda.transextends.service.TransFile;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

@Service
public class TransDocxImpl implements TransFile {

    @Autowired
    TransApi transApi;

    @Autowired
    ProgressDAO progressDAO;

    @Override
    public long calculateTotalProgress(String srcFile) {
        return 0;
    }

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) {
        try {
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(srcFile));
            Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
            while (itPara.hasNext()) {
                XWPFParagraph paragraph = itPara.next();
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (StrUtil.isNotBlank(text)) {
                        text = text.trim();
                        String transContent = transApi.translate(srcLang, desLang, text);
                        transContent += " ";
                        run.setText(transContent,0);
                        run.setText(transContent,0);
                    }
                }
            }
            Iterator<XWPFTable> itTable = document.getTablesIterator();
            while (itTable.hasNext()) {
                XWPFTable table = itTable.next();
                int count = table.getNumberOfRows();
                for (int i = 0; i < count; i++) {
                    XWPFTableRow row = table.getRow(i);
                    List<XWPFTableCell> cells = row.getTableCells();
                    for (XWPFTableCell cell : cells) {
                        String text = cell.getText();
                        if (StrUtil.isNotBlank(text)) {
                            List<XWPFParagraph> paragraphs = cell.getParagraphs();
                            for (XWPFParagraph paragraph : paragraphs) {
                                for (XWPFRun run : paragraph.getRuns()) {
                                    String text2 = run.getText(0);
                                    if (StrUtil.isNotBlank(text2)) {
                                        text2 = text2.trim();
                                        String transContent = transApi.translate(srcLang, desLang, text2);
                                        transContent += " ";
                                        run.setText(transContent,0);
                                        run.setText(transContent,0);
                                    }
                                }
                            }

                        }
                    }
                }
            }
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(desFile);
            document.write(outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
