package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.FormatConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransDocImpl implements TransFile {

    @Autowired
    TransDocxImpl transDocx;

    @Override
    public void translate(int rowId, String srcLang, String desLang, String srcFile, String desFile)  {
        try {
            String s = FormatConvert.convertDoc2Docx(srcFile);
            transDocx.translate(rowId, srcLang, desLang, s, desFile);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
