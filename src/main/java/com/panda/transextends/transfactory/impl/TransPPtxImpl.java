package com.panda.transextends.transfactory.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.transfactory.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class TransPPtxImpl implements TransFile {
    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    public long calculateTotalProgress(String srcFile) throws Exception {
        long total = 0;
        File file = new File(srcFile);
        try (InputStream is = new FileInputStream(file)) {
            SlideShow slideShow = null;
            String fe = FilenameUtils.getExtension(srcFile);
            if (fe.equalsIgnoreCase("ppt")) {
                slideShow = new HSLFSlideShow(is);
            } else {
                slideShow = new XMLSlideShow(is);
            }
            for (Slide slide : (List<Slide>) slideShow.getSlides()) {
                List shapes = slide.getShapes();
                if (shapes != null) {
                    for (Object shape : shapes) {
                        if (shape instanceof HSLFTextShape) {// 文本框
                            String text = ((HSLFTextShape) shape).getText();
                            if (StrUtil.isNotBlank(text)) {
                                total++;
                            }
                        } else if (shape instanceof XSLFTextShape) {// 文本框
                            String text = ((XSLFTextShape) shape).getText();
                            if (StrUtil.isNotBlank(text)) {
                                total++;
                            }
                        } else if (shape instanceof HSLFTable) {// 表格
                            int rowSize = ((HSLFTable) shape).getNumberOfRows();
                            int columnSize = ((HSLFTable) shape).getNumberOfColumns();
                            for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                    HSLFTableCell cell = ((HSLFTable) shape).getCell(rowNum, columnNum);
                                    if (cell != null) {
                                        String text = cell.getText();
                                        if (StrUtil.isNotBlank(text)) {
                                            total++;
                                        }
                                    }
                                }
                            }
                        } else if (shape instanceof XSLFTable) {// 表格
                            int rowSize = ((XSLFTable) shape).getNumberOfRows();
                            int columnSize = ((XSLFTable) shape).getNumberOfColumns();
                            for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                    XSLFTableCell cell = ((XSLFTable) shape).getCell(rowNum, columnNum);
                                    if (cell != null) {
                                        String text = cell.getText();
                                        if (StrUtil.isNotBlank(text)) {
                                            total++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            slideShow.close();
            return total;
        }
    }

    @Override
    public boolean translate(long rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        long total = calculateTotalProgress(srcFile);
        long current = 0;
        int percent = 0;
        File file = new File(srcFile);
        try (InputStream is = new FileInputStream(file);
             FileOutputStream os = new FileOutputStream(desFile)) {
            SlideShow slideShow = null;
            String fe = FilenameUtils.getExtension(srcFile);
            if (fe.equalsIgnoreCase("ppt")) {
                slideShow = new HSLFSlideShow(is);
            } else {
                slideShow = new XMLSlideShow(is);
            }
            // 一页一页读取
            for (Slide slide : (List<Slide>) slideShow.getSlides()) {
                List shapes = slide.getShapes();
                if (shapes != null) {
                    for (Object shape : shapes) {
                        if (shape instanceof HSLFTextShape) {// 文本框
                            String text = ((HSLFTextShape) shape).getText();
                            if (StrUtil.isNotBlank(text)) {
                                String transContent = coreApi.translate(srcLang, desLang, text);
                                ((HSLFTextShape) shape).setText(transContent);
                                current++;
                                if (percent != 100 * current / total) {
                                    percent = (int) (100 * current / total);
                                    if (percent > 100) percent = 100;
                                    recordDAO.updateProgress(rowId, percent);
                                }
                            }
                        } else if (shape instanceof XSLFTextShape) {// 文本框
                            String text = ((XSLFTextShape) shape).getText();
                            if (StrUtil.isNotBlank(text)) {
                                String transContent = coreApi.translate(srcLang, desLang, text);
                                ((XSLFTextShape) shape).setText(transContent);
                                current++;
                                if (percent != 100 * current / total) {
                                    percent = (int) (100 * current / total);
                                    if (percent > 100) percent = 100;
                                    recordDAO.updateProgress(rowId, percent);
                                }
                            }
                        } else if (shape instanceof HSLFTable) {// 表格
                            int rowSize = ((HSLFTable) shape).getNumberOfRows();
                            int columnSize = ((HSLFTable) shape).getNumberOfColumns();
                            for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                    HSLFTableCell cell = ((HSLFTable) shape).getCell(rowNum, columnNum);
                                    if (cell != null) {
                                        String text = cell.getText();
                                        if (StrUtil.isNotBlank(text)) {
                                            String transContent = coreApi.translate(srcLang, desLang, text);
                                            cell.setText(transContent);
                                            current++;
                                            if (percent != 100 * current / total) {
                                                percent = (int) (100 * current / total);
                                                if (percent > 100) percent = 100;
                                                recordDAO.updateProgress(rowId, percent);
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (shape instanceof XSLFTable) {// 表格
                            int rowSize = ((XSLFTable) shape).getNumberOfRows();
                            int columnSize = ((XSLFTable) shape).getNumberOfColumns();
                            for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                    XSLFTableCell cell = ((XSLFTable) shape).getCell(rowNum, columnNum);
                                    if (cell != null) {
                                        String text = cell.getText();
                                        if (StrUtil.isNotBlank(text)) {
                                            String transContent = coreApi.translate(srcLang, desLang, text);
                                            cell.setText(transContent);
                                            current++;
                                            if (percent != 100 * current / total) {
                                                percent = (int) (100 * current / total);
                                                if (percent > 100) percent = 100;
                                                recordDAO.updateProgress(rowId, percent);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            slideShow.write(os);
            slideShow.close();
            return true;
        }
    }
}
