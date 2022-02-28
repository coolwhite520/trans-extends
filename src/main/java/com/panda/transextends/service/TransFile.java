package com.panda.transextends.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface TransFile {
    void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile);
}
