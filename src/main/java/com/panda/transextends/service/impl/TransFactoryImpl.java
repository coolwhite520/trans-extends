package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFactory;
import com.panda.transextends.service.TransFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransFactoryImpl implements TransFactory {

    @Autowired
    TransDocImpl transDoc;

    @Autowired
    TransDocxImpl transDocx;

    @Autowired
    TransPPtImpl transPPt;

    @Autowired
    TransPPtxImpl transPPtx;

    @Autowired
    TransXlsImpl transXls;

    @Autowired
    TransXlsxImpl transXlsx;

    @Override
    public TransFile create(String ext) {
        if (ext.equals("doc")) {
            return transDoc;
        } else if (ext.equals("docx")) {
            return transDocx;
        } else if (ext.equals("ppt")) {
            return transPPt;
        } else if (ext.equals("pptx")) {
            return transPPtx;
        } else if (ext.equals("xls")) {
            return transXls;
        } else if (ext.equals("xlsx")) {
            return transXlsx;
        }
        return null;
    }
}
