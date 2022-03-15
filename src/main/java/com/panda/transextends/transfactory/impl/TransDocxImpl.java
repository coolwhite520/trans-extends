package com.panda.transextends.transfactory.impl;

import cn.hutool.core.util.StrUtil;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.transfactory.TransFile;
import com.panda.transextends.utils.CoreApi;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TransDocxImpl implements TransFile {

    @Autowired
    CoreApi coreApi;

    @Autowired
    RecordDAO recordDAO;

    /**
     * 存储读取到的图片 - docx
     *
     * @param pictures    图片集
     * @param picturePath 图片保存路径
     * @param imgNum      确定图片集中的某一张图片
     * @return 图片全路径
     * @throws IOException
     */
    private static String savePictureDocx(List<XWPFPictureData> pictures,
                                          String picturePath, int imgNum) throws IOException {
        File pictureFile = new File(picturePath);
        //保存路径不存在，则创建
        if (!pictureFile.exists()) {
            pictureFile.mkdirs();
        }
        if (pictures.size() != 0) {
            XWPFPictureData picture = pictures.get(imgNum);
            String rawName = picture.getFileName();
            String fileExt = rawName.substring(rawName.lastIndexOf("."));
            String newName = System.currentTimeMillis() + fileExt;
            String pictureName = picturePath + File.separator + newName;
            FileOutputStream fos = new FileOutputStream(pictureName);
            fos.write(picture.getData());
            fos.flush();
            fos.close();
            return pictureName;
        }
        return null;
    }

    /**
     * 删除临时存储的图片
     *
     * @param pictureName 图片全路径（含文件名）
     * @return
     */
    private static boolean deletePicture(String pictureName) {
        File pictureFile = new File(pictureName);
        // 目录此时为空，可以删除
        return pictureFile.delete();
    }

    public String removeAllPictures(String srcFile) throws IOException {
        int imgNum = 0;
        String strOutputFile = srcFile + ".rm-pic.docx";
        try (InputStream is = new FileInputStream(srcFile);
             FileOutputStream outStream = new FileOutputStream(strOutputFile);
             XWPFDocument docx = new XWPFDocument(is)) {
            //获取到该文档的所有段落集
            List<XWPFParagraph> paras = docx.getParagraphs();
            //获取到该文档的所有图片集
            List<XWPFPictureData> pictures = docx.getAllPictures();
            for (XWPFParagraph para : paras) {
                //段落中所有XWPFRun
                List<XWPFRun> runList = para.getRuns();
                ArrayList<Integer> indexs = new ArrayList<Integer>();
                for (int i = 0; i < runList.size(); i++) {
                    XWPFRun run = runList.get(i);
                    //判断该段落是否是图片
                    if (!run.getEmbeddedPictures().isEmpty()) {
                        //存储读取到的图片,并获取图片全路径
                        String pictureName = savePictureDocx(pictures, "/tmp", imgNum++);
                        //实际有图片才进行以下操作
                        if (pictureName != null) {
                            indexs.add(i);
                            deletePicture(pictureName);
                        }
                    }
                }
                for (Integer index : indexs) {
                    para.removeRun(index);
                }
                for (XWPFRun run : para.getRuns()) {
                    run.setFontSize(5);
                }
            }
            docx.write(outStream);
        }
        return strOutputFile;
    }

    public long calculateTotalProgress(String srcFile) throws Exception {
        long total = 0;
        try (InputStream is = new FileInputStream(srcFile);
             XWPFDocument document = new XWPFDocument(is)) {
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
            return total;
        }
    }

    @Override
    public boolean translate(long rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        long total = calculateTotalProgress(srcFile);
        long current = 0;
        int percent = 0;
        try (FileInputStream is = new FileInputStream(srcFile);
             FileOutputStream outStream = new FileOutputStream(desFile);
             XWPFDocument document = new XWPFDocument(is)) {
            Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
            while (itPara.hasNext()) {
                XWPFParagraph paragraph = itPara.next();
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (StrUtil.isNotBlank(text)) {
                        String transContent = "";
                        if (srcLang.equals(desLang)) {
                            // 语言相同就直接copy
                            transContent = text;
                        } else {
                            transContent = coreApi.translate(srcLang, desLang, text);
                            transContent += " ";
                        }
                        run.setText(transContent, 0);
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
                                        String transContent = "";
                                        if (srcLang.equals(desLang)) {
                                            // 语言相同就直接copy
                                            transContent = text2;
                                        } else {
                                            transContent = coreApi.translate(srcLang, desLang, text2);
                                            transContent += " ";
                                        }
                                        run.setText(transContent, 0);
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

                        }
                    }
                }
            }
            document.write(outStream);
        }

        return true;
    }
}
