package com.panda.transextends.controller;

import com.panda.transextends.constant.Constant;
import com.panda.transextends.mapper.RecordDAO;
import com.panda.transextends.pojo.Record;
import com.panda.transextends.pojo.ReqBody;
import com.panda.transextends.factory.TransFactory;
import com.panda.transextends.service.TransFileService;
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
    TransFileService transFileService;

    @PostMapping("/trans_file")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        return transFileService.TransFile(reqBody);
    }
}
