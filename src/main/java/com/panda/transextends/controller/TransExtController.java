package com.panda.transextends.controller;

import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.pojo.ReqBody;
import com.panda.transextends.service.TransFactory;
import com.panda.transextends.utils.BaseResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@Api(value = "", tags = {"翻译扩展接口"})
@RestController
@Slf4j
public class TransExtController {

    @Autowired
    TransFactory transFactory;

    @Autowired
    RecordDAO recordDAO;

    @PostMapping("/trans_file")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        try {
            File file = new File(reqBody.getSrcFile());
            if(!file.exists()){
                String format = String.format("在服务器找不到文件 => %s", reqBody.getSrcFile());
                return BaseResponse.error(format);
            }
            int transType = recordDAO.queryTransTypeByRowId(reqBody.getRowId());
            transType = 1;
            String fe = FilenameUtils.getExtension(reqBody.getSrcFile());
            boolean success = transFactory.create(transType, fe).translate(reqBody.getRowId(),
                    reqBody.getSrcLang(),
                    reqBody.getDesLang(),
                    reqBody.getSrcFile(),
                    reqBody.getDesFile());
            if (!success) {
                return BaseResponse.error("翻译失败");
            }
            return BaseResponse.ok();
        }catch (Exception e) {
            return BaseResponse.error(e.toString());
        }
    }
}
