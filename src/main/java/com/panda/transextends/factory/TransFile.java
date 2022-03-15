package com.panda.transextends.factory;

public interface TransFile {
    boolean translate(long rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception;
}
