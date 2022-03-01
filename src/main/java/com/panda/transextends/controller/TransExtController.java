package com.panda.transextends.controller;

import com.panda.transextends.mapper.RecordDAO;
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

    @Autowired
    RecordDAO recordDAO;

    @PostMapping("/trans_file")
    public BaseResponse translateFile(@RequestBody ReqBody reqBody) {
        try {
            int transType = recordDAO.queryTransTypeByRowId(reqBody.row_id);
            String fe = FilenameUtils.getExtension(reqBody.src_file);
            transFactory.create(transType, fe).translate(reqBody.row_id, reqBody.src_lang, reqBody.des_lang, reqBody.src_file, reqBody.des_file);
            return BaseResponse.ok();
        }catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error(e.getMessage());
        }
    }
}
