package com.xcg.serviceuser;

import com.xcg.freshcommon.core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


}
