package com.panda.transextends.controller;

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

    @PostMapping("/trans_office")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        try {
            File file = new File(reqBody.src_file);
            if (!file.exists()) {
                return BaseResponse.error(-100, "源文件不存在.");
            }
            String fe = FilenameUtils.getExtension(reqBody.src_file);
            transFactory.create(fe).translate(reqBody.row_id, reqBody.src_lang, reqBody.des_lang, reqBody.src_file, reqBody.des_file);
            return BaseResponse.ok();
        }catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error(e.getMessage());
        }
    }
}
