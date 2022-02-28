package com.panda.transextends.service.impl;

import com.panda.transextends.mapper.ProgressDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.TransApi;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class TransXlsxImpl implements TransFile {
    @Autowired
    TransApi transApi;

    @Autowired
    ProgressDAO progressDAO;

    public long calculateTotalProgress(String srcFile) {
        long total = 0;
        FileInputStream is = null;
        Workbook workbook = null;
        try {
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
                            total ++;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (is != null) {
                    is.close();
                }
                return total;
            } catch (IOException e) {
            }
        }
        return 0;
    }


    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) {
        FileInputStream is = null;
        Workbook workbook = null;
        try {
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
                            String content = cell.getStringCellValue();
                            String transContent = transApi.translate(srcLang, desLang, content);
                            cell.setCellValue(transContent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    FileOutputStream outStream;
                    outStream = new FileOutputStream(desFile);
                    workbook.write(outStream);
                    workbook.close();
                    outStream.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
