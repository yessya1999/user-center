package com.yess.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yess.usercenter.common.ErrorCode;
import com.yess.usercenter.constant.UserConstant;
import com.yess.usercenter.exception.BusinessException;
import com.yess.usercenter.model.User;
import com.yess.usercenter.service.UserService;
import com.yess.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author yess
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-07-02 16:45:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值，用于混淆加密
     */
    private static final  String SALT = "yess";

    @Resource
    UserMapper userMapper;


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1.校验账户和密码
        //非空
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户、密码、校验密码、星球编号为空");
        }
        //账户长度不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于4位");
        }

        //密码长度不小于8位
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        //账户不包含特殊字符
        String validPattern = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户有特殊字符");
        }

        //星球编号只能是长度不超过5位的数字
        validPattern = "^[0-9]{1,5}$";
        matcher = Pattern.compile(validPattern).matcher(planetCode);
        if(!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号输入有误");
        }

        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }

        //账户不能重复
        QueryWrapper<User> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("userAccount", userAccount);
        Integer result = userMapper.selectCount(userAccountQueryWrapper);
        if(result > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }

        //星球编号唯一
        QueryWrapper<User> planetCodeQueryWrapper = new QueryWrapper<>();
        planetCodeQueryWrapper.eq("planetCode", planetCode);
        result = userMapper.selectCount(planetCodeQueryWrapper);
        if(result > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已经被占用");
        }

        //2.加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        //3.用户数据插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存到数据库时异常");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        //1.数据有效性校验
        //非空
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或者密码为空");
        }
        //账户长度不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于4位");
        }

        //密码长度不小于8位
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        //账户不包含特殊字符
        String validPattern = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }

        //2.验证密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword!");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码输入错误");
        }

        //3.返回的用户信息脱敏
        User safetyUser = this.getSafetyUser(user);

        //4.记录用户的登录态（session）
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest httpServletRequest) {
        if(httpServletRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无请求");
        }
        httpServletRequest.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 返回用户信息脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有返回的用户");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }
}




