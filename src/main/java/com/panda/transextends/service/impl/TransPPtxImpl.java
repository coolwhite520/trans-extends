package com.panda.transextends.service.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.ProgressDAO;
import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.TransApi;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class TransPPtxImpl implements TransFile {
    @Autowired
    TransApi transApi;

    @Autowired
    ProgressDAO progressDAO;

    public long calculateTotalProgress(String srcFile) {
        long total = 0;
        InputStream is = null;
        SlideShow slideShow = null;
        try {
            String fe = FilenameUtils.getExtension(srcFile);
            File file = new File(srcFile);
            is = new FileInputStream(file);
            if (fe.equalsIgnoreCase("ppt")) {
                slideShow = new HSLFSlideShow(is);
            } else {
                slideShow = new XMLSlideShow(is);
            }
            if (slideShow != null) {
                // 一页一页读取
                for (Slide slide : (List<Slide>) slideShow.getSlides()) {
                    List shapes = slide.getShapes();
                    if (shapes != null) {
                        for (int i = 0; i < shapes.size(); i++) {
                            Shape shape = (Shape) shapes.get(i);
                            if (shape instanceof HSLFTextShape) {// 文本框
                                String text = ((HSLFTextShape) shape).getText();
                                if (StrUtil.isNotBlank(text)) {
                                    total++;
                                }
                            }
                            else if (shape instanceof XSLFTextShape) {// 文本框
                                String text = ((XSLFTextShape) shape).getText();
                                if (StrUtil.isNotBlank(text)) {
                                    total++;
                                }
                            }
                            else if (shape instanceof HSLFTable) {// 表格
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
                            }
                            else if (shape instanceof XSLFTable) {// 表格
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

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (slideShow != null) {
                    slideShow.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return 0;
    }

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) {
        InputStream is = null;
        SlideShow slideShow = null;
        try {
            String fe = FilenameUtils.getExtension(srcFile);
            File file = new File(srcFile);
            is = new FileInputStream(file);
            if (fe.equalsIgnoreCase("ppt")) {
                slideShow = new HSLFSlideShow(is);
            } else {
                slideShow = new XMLSlideShow(is);
            }
            if (slideShow != null) {
                // 一页一页读取
                for (Slide slide : (List<Slide>) slideShow.getSlides()) {
                    List shapes = slide.getShapes();
                    if (shapes != null) {
                        for (int i = 0; i < shapes.size(); i++) {
                            Shape shape = (Shape) shapes.get(i);
                            if (shape instanceof HSLFTextShape) {// 文本框
                                String text = ((HSLFTextShape) shape).getText();
                                if (StrUtil.isNotBlank(text)) {
                                    String transContent = transApi.translate(srcLang, desLang, text);
                                    ((HSLFTextShape) shape).setText(transContent);
                                }
                            }
                            else if (shape instanceof XSLFTextShape) {// 文本框
                                String text = ((XSLFTextShape) shape).getText();
                                if (StrUtil.isNotBlank(text)) {
                                    String transContent = transApi.translate(srcLang, desLang, text);
                                    ((HSLFTextShape) shape).setText(transContent);
                                }
                            }
                            else if (shape instanceof HSLFTable) {// 表格
                                int rowSize = ((HSLFTable) shape).getNumberOfRows();
                                int columnSize = ((HSLFTable) shape).getNumberOfColumns();
                                for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                    for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                        HSLFTableCell cell = ((HSLFTable) shape).getCell(rowNum, columnNum);
                                        if (cell != null) {
                                            String text = cell.getText();
                                            if (StrUtil.isNotBlank(text)) {
                                                String transContent = transApi.translate(srcLang, desLang, text);
                                                ((HSLFTextShape) shape).setText(transContent);
                                            }
                                        }
                                    }
                                }
                            }
                            else if (shape instanceof XSLFTable) {// 表格
                                int rowSize = ((XSLFTable) shape).getNumberOfRows();
                                int columnSize = ((XSLFTable) shape).getNumberOfColumns();
                                for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                                    for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                        XSLFTableCell cell = ((XSLFTable) shape).getCell(rowNum, columnNum);
                                        if (cell != null) {
                                            String text = cell.getText();
                                            if (StrUtil.isNotBlank(text)) {
                                                String transContent = transApi.translate(srcLang, desLang, text);
                                                ((HSLFTextShape) shape).setText(transContent);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (slideShow != null) {
                    FileOutputStream outStream;
                    outStream = new FileOutputStream(desFile);
                    slideShow.write(outStream);
                    slideShow.close();
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
