package com.panda.transextends;

import com.panda.transextends.service.impl.TransTikaImpl;
import com.panda.transextends.utils.CoreApi;
import com.panda.transextends.utils.RedisUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TransTikaImpl transTika;

    @Test
    void contextLoads() throws Exception {
        transTika.translate(70, "Chinese", "", "/Users/baiyang/Desktop/翻译资源/a.docx", "/Users/baiyang/Desktop/翻译资源/aa123.docx");
    }

}
