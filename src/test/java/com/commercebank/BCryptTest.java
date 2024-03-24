package com.commercebank;
import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;

public class BCryptTest {

    @Test
    public void test(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String rawPassword1 = "123456";
        String encodePassword1 = bCryptPasswordEncoder.encode(rawPassword1);
        System.out.println(rawPassword1 + " ->> "+ encodePassword1);
        String encodePassword2 = bCryptPasswordEncoder.encode(rawPassword1);
        System.out.println(rawPassword1 + " ->> "+ encodePassword2);
        System.out.println(bCryptPasswordEncoder.matches(rawPassword1,encodePassword2));
    }
}
