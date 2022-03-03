package com.panda.transextends.service.impl;

import com.aspose.words.SaveFormat;
import com.panda.transextends.service.TransFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;

@Service
public class TransDocImpl implements TransFile {

    @Autowired
    TransDocxImpl transDocx;

    // 将doc输入流转换为docx输入流
    public static InputStream convertDocIs2DocxIs(InputStream docInputStream) throws IOException {
        byte[] docBytes = FileCopyUtils.copyToByteArray(docInputStream);
        byte[] docxBytes = convertDocStream2docxStream(docBytes);
        return new ByteArrayInputStream(docxBytes);
    }
    // 将doc字节数组转换为docx字节数组
    private static byte[] convertDocStream2docxStream(byte[] arrays) {
        byte[] docxBytes = new byte[1];
        if (arrays != null && arrays.length > 0) {
            try (
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    InputStream sbs = new ByteArrayInputStream(arrays)
            ) {
                com.aspose.words.Document doc = new com.aspose.words.Document(sbs);
                doc.save(os, SaveFormat.DOCX);
                docxBytes = os.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return docxBytes;
    }
    private boolean convertDoc2Docx(String srcFile, String desFile) {
        InputStream is = null;
        try {
            is = new FileInputStream(srcFile);
            InputStream convertDocIs2DocxIs = convertDocIs2DocxIs(is);
            XWPFDocument xwpfDocument = new XWPFDocument(convertDocIs2DocxIs);
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(desFile);
            xwpfDocument.write(outStream);
            xwpfDocument.close();
            outStream.close();
            return true;

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        boolean b = convertDoc2Docx(srcFile, srcFile + "x");
        if (b) return transDocx.translate(rowId, srcLang, desLang, srcFile + "x", desFile);
        return false;
    }
}
