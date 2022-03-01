package com.panda.transextends;

import com.panda.transextends.utils.RedisUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    void contextLoads() {
        String srcFile = "/a/b/c/aaaa.txt.doc";
        String path = FilenameUtils.getPath(srcFile);
        String baseName = FilenameUtils.getBaseName(srcFile);
        System.out.println(path);
        System.out.println(baseName);
    }

}
