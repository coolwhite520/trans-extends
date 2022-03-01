package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.TransApi;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
public class TransXlsxImpl implements TransFile {
    @Autowired
    TransApi transApi;

    @Autowired
    RecordDAO recordDAO;

    public long calculateTotalProgress(String srcFile) throws Exception {
        long total = 0;
        FileInputStream is = null;
        Workbook workbook = null;
        String fe = FilenameUtils.getExtension(srcFile);
        is = new FileInputStream(srcFile);
        if (fe.equalsIgnoreCase("xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            int rows = sheetAt.getLastRowNum();
            for (int j = 0; j < rows; j++) {
                Row row = sheetAt.getRow(j);
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                for (int k = 0; k < physicalNumberOfCells; k++) {
                    Cell cell = row.getCell(k);
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        String text = cell.getStringCellValue();
                        if (StrUtil.isNotBlank(text)) {
                            total++;
                        }
                    }
                }
            }
        }
        workbook.close();
        is.close();
        return total;

    }


    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        long total = calculateTotalProgress(srcFile);
        long current = 0;
        int percent = 0;
        FileInputStream is = null;
        Workbook workbook = null;
        String fe = FilenameUtils.getExtension(srcFile);
        is = new FileInputStream(srcFile);
        if (fe.equalsIgnoreCase("xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            workbook = new HSSFWorkbook(is);
        }
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheetAt = workbook.getSheetAt(i);
            int rows = sheetAt.getLastRowNum();
            for (int j = 0; j < rows; j++) {
                Row row = sheetAt.getRow(j);
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                for (int k = 0; k < physicalNumberOfCells; k++) {
                    Cell cell = row.getCell(k);
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        String text = cell.getStringCellValue();
                        if (StrUtil.isNotBlank(text)) {
                            String transContent = transApi.translate(srcLang, desLang, text);
                            cell.setCellValue(transContent);
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
        FileOutputStream outStream;
        outStream = new FileOutputStream(desFile);
        workbook.write(outStream);
        workbook.close();
        outStream.close();
        is.close();
        return true;
    }
}
