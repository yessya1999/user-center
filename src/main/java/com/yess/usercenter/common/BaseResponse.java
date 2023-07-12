package com.yess.usercenter.common;

import lombok.Data;

import java.io.PrintStream;
import java.io.Serializable;
import java.security.PrivateKey;

/**
 * 通用返回前端类
 *
 * @author yess
 * @param <T>   T代表返回的data数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -1125368669323193745L;

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
