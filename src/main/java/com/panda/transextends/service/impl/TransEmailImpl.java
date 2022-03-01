package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.PluginsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransEmailImpl implements TransFile {

    @Autowired
    PluginsApi pluginsApi;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
//        托管给plugins容器用python模块进行处理,plugins模块采用python编写，能用java处理的就在当前模块处理，处理不了的就仍出去。
        return pluginsApi.translate_email(rowId, srcLang, desLang, srcFile, desFile);
    }
}
