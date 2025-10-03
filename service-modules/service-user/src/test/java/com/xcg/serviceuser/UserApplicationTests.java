package com.xcg.serviceuser;

import com.xcg.freshcommon.core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class UserApplicationTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void contextLoads() {}

    @Test
    public void testJwt() {

    	String token = jwtUtil.generateToken(1L, "xcg");
    	System.out.println(token);
    	Claims claims = jwtUtil.parseToken(token);
    	System.out.println(claims.getSubject());
        System.out.println(jwtUtil.getUserIdFromToken(token));
    }

    @Test
    public void testPwd(){
    // e10adc3949ba59abbe56e057f20f883e
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(s);
    }
}
