package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

@Service
public class TransDocxImpl implements TransFile {

    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    public long calculateTotalProgress(String srcFile) throws Exception {
        long total = 0;
        XWPFDocument document = null;
        document = new XWPFDocument(POIXMLDocument.openPackage(srcFile));
        Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = itPara.next();
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (StrUtil.isNotBlank(text)) {
                    total++;
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
                                    total++;
                                }
                            }
                        }

                    }
                }
            }
        }
        document.close();
        return total;

    }

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        long total = calculateTotalProgress(srcFile);
        long current = 0;
        int percent = 0;
        XWPFDocument document = null;
        document = new XWPFDocument(POIXMLDocument.openPackage(srcFile));
        Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = itPara.next();
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (StrUtil.isNotBlank(text)) {
                    String transContent = coreApi.translate(srcLang, desLang, text);
                    transContent += " ";
                    run.setText(transContent, 0);
                    run.setText(transContent, 0);
                    current++;
                    if (percent != 100 * current/total) {
                        percent = (int) (100 * current/total);
                        if (percent >100) percent = 100;
                        recordDAO.updateProgress(rowId, percent);
                    }
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
                                    String transContent = coreApi.translate(srcLang, desLang, text2);
                                    transContent += " ";
                                    run.setText(transContent, 0);
                                    run.setText(transContent, 0);
                                    current++;
                                    if (percent != 100 * current/total) {
                                        percent = (int) (100 * current/total);
                                        if (percent >100) percent = 100;
                                        recordDAO.updateProgress(rowId, percent);
                                    }
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
        document.close();
        outStream.close();
        return true;
    }
}
