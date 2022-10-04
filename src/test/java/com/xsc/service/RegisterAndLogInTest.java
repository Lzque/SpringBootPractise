package com.xsc.service;

import com.xsc.SpringBootUnitTest;
import com.xsc.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RegisterAndLogInTest extends SpringBootUnitTest {
    @Autowired
    private RegisterAndLogIn registerAndLogIn;

    @Test
    void testSaveImage() {
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setUserName("买家");
        user.setPhoneNumber("15177516666");
        user.setUserPassword("123456");
        user.setAvatarData(new byte[]{1, 2, 3});
        boolean register = registerAndLogIn.register(user);
        Assertions.assertFalse(register);
        user.setPhoneNumber("15177777777");
        register = registerAndLogIn.register(user);
        Assertions.assertTrue(register);
    }

    @Test
    void testLogIn() {
        User user = new User();
        user.setPhoneNumber("15177516666");
        user.setUserPassword("123456");
        Integer integer = registerAndLogIn.logIn(user);
        Assertions.assertTrue(integer > 0);
        user.setUserPassword("666666");
        integer = registerAndLogIn.logIn(user);
        Assertions.assertEquals(-1, (int) integer);
    }
}
