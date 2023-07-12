package com.yess.usercenter.common;

import com.yess.usercenter.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 创建通用返回对象的工具类
 *
 * @author yess
 */
public class ResultUtils {

    /**
     * 返回运行成功工具类
     *
     * @param data  返回数据对象
     * @param <T>
     * @return  通用返回成功对象
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok", "");
    }

    /**
     * 返回错误的工具类
     *
     * @param errorCode
     * @param <T>
     * @return  封装错误的通用返回对象
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(int code, String message, String description){
        return new BaseResponse<>(code, null, message, description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, RuntimeException e){
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
