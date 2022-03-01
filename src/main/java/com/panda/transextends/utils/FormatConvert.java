package com.panda.transextends.utils;

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

