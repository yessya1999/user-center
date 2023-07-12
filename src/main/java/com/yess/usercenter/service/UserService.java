package com.yess.usercenter.service;

import com.yess.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author yess
* @description 针对表【user】的数据库操作Service
* @createDate 2023-07-02 16:45:04
*/
public interface UserService extends IService<User> {


    /**
     *注册功能
     *
     * @param userAccount   用户名
     * @param userPassword  用户密码
     * @param checkPassword 重复输入的密码
     * @return  返回新用户的id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 登录功能
     *
     * @param userAccount   用户名
     * @param userPassword  用户密码
     * @param httpServletRequest
     * @return  返回登陆的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户注销功能
     *
     * @param httpServletRequest
     * @return
     */
    Integer userLogout(HttpServletRequest httpServletRequest);

    /**
     * 返回用户信息脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);
}
