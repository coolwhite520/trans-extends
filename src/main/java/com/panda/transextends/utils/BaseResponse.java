package com.panda.transextends.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 3296878552984277243L;
    /**
     * 状态码
     */
    private int code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应内容
     */
    private T data;

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
    }

    public static <T> BaseResponse<T> ok() {
        return new BaseResponse<T>(HttpStatus.OK);
    }

    public static <T> BaseResponse<T> ok(HttpStatus status, T data) {
        return new BaseResponse<T>(status.value(), status.getReasonPhrase(), data);
    }

    public static <T> BaseResponse<T> ok(String message) {
        return new BaseResponse<T>(HttpStatus.OK.value(), message);
    }

    public static <T> BaseResponse<T> ok(String message, T date) {
        return new BaseResponse<T>(HttpStatus.OK.value(), message, date);
    }

    public static <T> BaseResponse<T> ok(T date) {
        return new BaseResponse<T>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), date);
    }

    public static <T> BaseResponse<T> error(Integer code, String message) {
        return new BaseResponse<T>(code, message);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<T>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static <T> BaseResponse<T> error(HttpStatus status) {
        return new BaseResponse<T>(status.value(), status.getReasonPhrase());
    }

}
