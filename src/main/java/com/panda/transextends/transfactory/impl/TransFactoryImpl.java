package com.panda.transextends.transfactory.impl;

import com.panda.transextends.transfactory.TransFactory;
import com.panda.transextends.transfactory.TransFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
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
    TransImagesImpl transImages; // 直接返回文本内容

    @Autowired
    TransImg2Impl transImg2; // 先转为可编辑的pdf -> docx

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
                // 不调用plugins进行翻译处理了，python的那个处理有问题，没有java自己的库解析好
                return transEmail;
            } else {//其他类型的文件让tika解析
                return transTika;
            }
        } else {
            String format = String.format("未知的文件类型：transType：%d", transType);
            throw new RuntimeException(format);
        }
    }
}
