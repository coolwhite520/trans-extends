package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import org.springframework.stereotype.Service;

@Service
public class TransTikaImpl implements TransFile {
    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        return false;
    }
}
