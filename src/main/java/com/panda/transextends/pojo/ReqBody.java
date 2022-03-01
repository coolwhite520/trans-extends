package com.panda.transextends.pojo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReqBody implements Serializable {
    private static final long serialVersionUID = -4570774199673796187L;
    int rowId;
    String srcLang;
    String desLang;
    String srcFile;
    String desFile;
}
