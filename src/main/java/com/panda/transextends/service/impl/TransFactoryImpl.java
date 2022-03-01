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
                // 内部集成了transDocx组件，先转换后调用transDocx翻译处理
                return transDoc;
            } else if (ext.equalsIgnoreCase("docx")) {
                return transDocx;
            }  else if (ext.equalsIgnoreCase("pptx") || ext.equalsIgnoreCase("ppt")) {
                // bean内部做了兼容处理，可以同时支持ppt和pptx，输出后缀和输入后缀相同
                return transPPtx;
            }  else if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {
                // bean内部做了兼容处理，可以同时支持xls和xlsx，输出后缀和输入后缀相同
                return transXlsx;
            } else if (ext.equalsIgnoreCase("pdf")) {
                // 内部调用plugins的convert，然后调用transDocx进行翻译处理
                return transPDF;
            } else if (ext.equalsIgnoreCase("eml")) {
                // 直接调用plugins进行翻译处理
                return transEmail;
            }
        } else {//其他类型的文件让tika解析
            return transTika;
        }
        return null;
    }
}
