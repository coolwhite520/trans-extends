package com.panda.transextends.utils;

//import com.aspose.words.SaveFormat;
import org.springframework.util.FileCopyUtils;

import java.io.*;

public class FormatConvert {
    public static String convertDoc2Docx(String srcFile) {
        try {
            return srcFile + "x";
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String convertPPt2PPtx(String srcFile) {
        try {
            return srcFile + "x";
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String convertXls2Xlsx(String srcFile) {
        try {
            return srcFile + "x";
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

//
//    // 将doc输入流转换为docx输入流
//    public static InputStream convertDocIs2DocxIs(InputStream docInputStream) throws IOException {
//        byte[] docBytes = FileCopyUtils.copyToByteArray(docInputStream);
//        byte[] docxBytes = convertDocStream2docxStream(docBytes);
//        return new ByteArrayInputStream(docxBytes);
//    }
//    // 将doc字节数组转换为docx字节数组
//    private static byte[] convertDocStream2docxStream(byte[] arrays) {
//        byte[] docxBytes = new byte[1];
//        if (arrays != null && arrays.length > 0) {
//            try (
//                    ByteArrayOutputStream os = new ByteArrayOutputStream();
//                    InputStream sbs = new ByteArrayInputStream(arrays)
//            ) {
//                com.aspose.words.Document doc = new com.aspose.words.Document(sbs);
//                doc.save(os, SaveFormat.DOCX);
//                docxBytes = os.toByteArray();
//            } catch (Exception e) {
//
//                System.out.println("出错啦");
//            }
//        }
//        return docxBytes;
//    }
