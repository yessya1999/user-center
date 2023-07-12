package com.yess.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    public void testRigest(){
        String encryptPassword = DigestUtils.md5DigestAsHex(("salt + userPassword").getBytes(StandardCharsets.UTF_8));
        System.out.println(encryptPassword);
    }

    @Test
    void contextLoads() {
    }

}
