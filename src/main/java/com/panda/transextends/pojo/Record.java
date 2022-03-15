package com.panda.transextends.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Record implements Serializable {
    private static final long serialVersionUID = 7987743643206353138L;
    long Id;                        //int64       `json:"id"`
    String Sha1;                    //string      `json:"sha1"` // 文本或文件的sha1
    String Content;                 //string      `json:"content"`
    String ContentType;             //string      `json:"content_type"` // text , application/zip, image/png ,
    int TransType;                  //int         `json:"trans_type"`   // 0: 文本 , 1：图片  2: 文档
    String OutputContent;           //string      `json:"output_content"`
    String SrcLang;                 //string      `json:"src_lang"`
    String DesLang;                 //string      `json:"des_lang"`
    String FileName;                //string      `json:"file_name"`   // 去掉后缀名后的结果
    String FileExt;                 //string      `json:"file_ext"`
    String DirRandId;               //string      `json:"dir_rand_id"`
    int Progress;                   //int         `json:"progress"`
    int State;                      //TransStatus `json:"state"`
    String StateDescribe;           //string      `json:"state_describe"`
    String Error;                   //string      `json:"error"`
    long UserId;                    //int64       `json:"user_id"`
    String OutFileExt;              //string      `json:"out_file_ext"` // 输出文件扩展名
    String StartAt;                 //string      `json:"start_at"`
    String EndAt;                   //string      `json:"end_at"`
    String CreateAt;                //string      `json:"create_at"`   //
}
