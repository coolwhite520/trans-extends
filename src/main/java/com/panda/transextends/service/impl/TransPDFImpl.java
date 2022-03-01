package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.ConvertApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransPDFImpl implements TransFile {

    @Autowired
    ConvertApi convertApi;

    @Autowired
    TransDocxImpl transDocx;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {

        return false;
    }
}
