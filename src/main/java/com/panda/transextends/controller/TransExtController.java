package com.panda.transextends.controller;

import com.panda.transextends.pojo.ReqBody;
import com.panda.transextends.service.TransFileService;
import com.panda.transextends.utils.BaseResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(value = "", tags = {"翻译扩展接口"})
@RestController
@Slf4j
public class TransExtController {

    @Autowired
    TransFileService transFileService;

    @PostMapping("/trans_file")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        return transFileService.TransFile(reqBody);
    }
}
