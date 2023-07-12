package com.yess.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegistRequest implements Serializable {


    private static final long serialVersionUID = -1146170243199053241L;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 二次输入校验密码
     */
    private String checkPassword;

    /**
     * 星球编号
     */
    private String planetCode;
}
