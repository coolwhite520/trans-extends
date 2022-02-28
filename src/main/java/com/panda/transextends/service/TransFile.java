package com.panda.transextends.service;

public interface TransFile {
    long calculateTotalProgress(String srcFile);
    void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile);
}
