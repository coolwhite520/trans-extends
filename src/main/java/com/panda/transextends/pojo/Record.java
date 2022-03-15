package com.panda.transextends.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Record implements Serializable {
    private static final long serialVersionUID = 7987743643206353138L;
    long id;                        //int64       `json:"id"`
    String sha1;                    //string      `json:"sha1"` // 文本或文件的sha1
    String content;                 //string      `json:"content"`
    String contentType;             //string      `json:"content_type"` // text , application/zip, image/png ,
    int transType;                  //int         `json:"trans_type"`   // 0: 文本 , 1：图片  2: 文档
    String outputContent;           //string      `json:"output_content"`
    String srcLang;                 //string      `json:"src_lang"`
    String desLang;                 //string      `json:"des_lang"`
    String fileName;                //string      `json:"file_name"`   // 去掉后缀名后的结果
    String fileExt;                 //string      `json:"file_ext"`
    String dirRandId;               //string      `json:"dir_rand_id"`
    int progress;                   //int         `json:"progress"`
    int state;                      //TransStatus `json:"state"`
    String stateDescribe;           //string      `json:"state_describe"`
    String error;                   //string      `json:"error"`
    long userId;                    //int64       `json:"user_id"`
    String outFileExt;              //string      `json:"out_file_ext"` // 输出文件扩展名
    String startAt;                 //string      `json:"start_at"`
    String endAt;                   //string      `json:"end_at"`
    String createAt;                //string      `json:"create_at"`   //
}
