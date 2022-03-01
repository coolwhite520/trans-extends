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
    TransPPtxImpl transPPtx;

    @Autowired
    TransXlsxImpl transXlsx;

    @Autowired
    TransPDFImpl transPDF;

    @Autowired
    TransEmailImpl transEmail;

    @Autowired
    TransImagesImpl transImages;

    @Autowired
    TransTikaImpl transTika;

    @Override
    public TransFile create(int transType, String ext) {
        if (transType == 1) {//图片
            return transImages;
        }
        else if (transType == 2) {//可以输出格式的文件
            if (ext.equalsIgnoreCase("doc")) {
                return transDoc;
            } else if (ext.equalsIgnoreCase("docx")) {
                return transDocx;
            }  else if (ext.equalsIgnoreCase("pptx") || ext.equalsIgnoreCase("ppt")) {
                return transPPtx;
            }  else if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {
                return transXlsx;
            } else if (ext.equalsIgnoreCase("pdf")) {
                return transPDF;
            } else if (ext.equalsIgnoreCase("eml")) {
                return transEmail;
            }
        } else {
            return transTika;
        }
        return null;
    }
}
