package com.yess.usercenter.service;
import java.util.Date;

import com.yess.usercenter.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 对用户服务的测试
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    public UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("yess");
        user.setUserAccount("admin");
        user.setAvatarUrl("https://i0.hdslb.com/bfs/face/aabb00d3fa8b1526879ee5fc3875c5f543100aa2.jpg@96w_96h_1c_1s_!web-avatar.webp");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("456");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {

        String userAccount = " ";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "2";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "yu pi";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        checkPassword = "413241232";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "hu66";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(result < 0);

        planetCode = "13412412";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }
}