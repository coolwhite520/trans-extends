package com.panda.transextends.controller;

import lombok.Data;

@Data
public class ReqBody {
    int row_id;
    String src_lang;
    String des_lang;
    String src_file;
    String des_file;
}
