package com.panda.transextends;

import com.panda.transextends.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransExtendsApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    void contextLoads() {
        String hahaa = redisUtil.get("hahaa");
        System.out.println(hahaa);
    }

}
