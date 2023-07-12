package com.yess.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yess.usercenter.common.BaseResponse;
import com.yess.usercenter.common.ErrorCode;
import com.yess.usercenter.common.ResultUtils;
import com.yess.usercenter.constant.UserConstant;
import com.yess.usercenter.exception.BusinessException;
import com.yess.usercenter.model.User;
import com.yess.usercenter.model.request.UserLoginRequest;
import com.yess.usercenter.model.request.UserRegistRequest;
import com.yess.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegistRequest userRegistRequest){

        if(userRegistRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求体为空");
        }
        String userAccount = userRegistRequest.getUserAccount();
        String userPassword = userRegistRequest.getUserPassword();
        String checkPassword = userRegistRequest.getCheckPassword();
        String planetCode = userRegistRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);

        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){

        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求体为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        User user = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogin(HttpServletRequest httpServletRequest){
        if(httpServletRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        Integer result = userService.userLogout(httpServletRequest);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        Object userObj = session.getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录或用户过期");
        }
        //TODO 校验用户是否合法
        User user = userService.getById(currentUser.getId());
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest httpServletRequest){
        //仅限管理员查询
        //仅限管理员删除
        if(!this.isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH, "不是管理员");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);

        List<User> users = userList.stream().map(user -> {
                    return userService.getSafetyUser(user);
                }
        ).collect(Collectors.toList());
        return ResultUtils.success(users);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest httpServletRequest){
        //仅限管理员删除
        if(!this.isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH, "不是管理员");
        }

        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id输入有误");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 判断用户是否为管理员
     * @param httpServletRequest
     * @return 是-true 不是-false
     */
    private boolean isAdmin(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        Object userObj = session.getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }
}
