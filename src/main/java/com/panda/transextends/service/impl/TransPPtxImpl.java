package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import org.springframework.stereotype.Service;

@Service
public class TransPPtxImpl implements TransFile {

    public long calculateTotalProgress(String srcFile) {
        return 0;
    }

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) {

    }
}
