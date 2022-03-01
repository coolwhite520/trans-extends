package com.panda.transextends.service.impl;

import com.panda.transextends.service.TransFile;
import com.panda.transextends.utils.PluginsApi;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransPDFImpl implements TransFile {

    @Autowired
    PluginsApi pluginsApi;

    @Autowired
    TransDocxImpl transDocx;

    @Override
    public boolean translate(int rowId, String srcLang, String desLang, String srcFile, String desFile) throws Exception {
        boolean p2dx = pluginsApi.convert(srcFile, "p2dx");
        if (p2dx) {
            String path = FilenameUtils.getFullPathNoEndSeparator(srcFile);
            String baseName = FilenameUtils.getBaseName(srcFile);
            String srcConFile = String.format("%s/%s.docx", path, baseName);
            return transDocx.translate(rowId, srcLang, desLang, srcConFile, desFile);
        }
        return false;
    }
}
