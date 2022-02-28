package com.panda.transextends.controller;

import com.panda.transextends.service.TransFactory;
import com.panda.transextends.utils.BaseResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FilenameUtils;

@Api(value = "", tags = {"翻译扩展接口"})
@RestController
@Slf4j
public class TransExtController {

    @Autowired
    TransFactory transFactory;

    @PostMapping("/trans_file")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        String fe = FilenameUtils.getExtension(reqBody.src_file);
        transFactory.create(fe).translate(reqBody.row_id, reqBody.src_lang, reqBody.des_lang, reqBody.src_file, reqBody.des_file);
        return BaseResponse.ok(reqBody);
    }

}
