package com.panda.transextends.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OcrEntity implements Serializable {
    private static final long serialVersionUID = -1178442489360736998L;
    int left;
    int top;
    int right;
    int bottom;
    String content;
}
