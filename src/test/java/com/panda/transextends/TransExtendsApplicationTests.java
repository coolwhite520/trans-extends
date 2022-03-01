package com.panda.transextends;

import com.panda.transextends.pojo.OcrEntity;
import com.panda.transextends.service.impl.TransImagesImpl;
import com.panda.transextends.service.impl.TransTikaImpl;
import com.panda.transextends.utils.CoreApi;
import com.panda.transextends.utils.OcrApi;
import com.panda.transextends.utils.RedisUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TransTikaImpl transTika;

    @Autowired
    OcrApi ocrApi;

    @Test
    void contextLoads() throws Exception {
//        transTika.translate(70, "Chinese", "", "/Users/baiyang/Desktop/翻译资源/a.docx", "/Users/baiyang/Desktop/翻译资源/aa123.docx");
        List<OcrEntity> lines = ocrApi.extract("Chinese", "/Users/baiyang/Desktop/翻译资源/bbb.jpg");
        for (OcrEntity line : lines) {
            System.out.println(line.toString());
        }
    }

}
