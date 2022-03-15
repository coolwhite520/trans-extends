package com.panda.transextends.constant;

public enum TransStatus {
    TransNoRun(0, "上传成功未翻译"),
    TransBeginTranslate(1, "正在进行翻译"),
    TransTranslateFailed(2, "翻译失败"),
    TransTranslateSuccess(3, "翻译成功");

    private int code;
    private String message;

    TransStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}
