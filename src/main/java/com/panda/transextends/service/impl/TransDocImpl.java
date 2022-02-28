package com.panda.transextends.service.impl;

import com.panda.transextends.mapper.ProgressDAO;
import com.panda.transextends.service.TransFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransDocImpl implements TransFile {

    @Autowired
    ProgressDAO progressDAO;

    @Override
    public long calculateTotalProgress(String srcFile) {
        return 0;
    }

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) {

    }
}
