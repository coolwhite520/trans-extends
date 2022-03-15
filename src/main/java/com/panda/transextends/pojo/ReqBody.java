package com.panda.transextends.pojo;

import lombok.Data;

@Data
public class ReqBody {
    long id;
    String dataAbsPath; // 因为本模块在容器中，无法拿到data的绝对路径，所以需要主服务传递过来
}
