package com.xsc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class RegisterAndLogInControllerTest extends ControllerBaseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister() throws Exception {
        User user = new User();
        user.setUserName("买家");
        user.setPhoneNumber("15177516666");
        user.setUserPassword("333333");
        user.setPaymentPassword("123456");
        user.setAddress("无");
        user.setWallet(true);
        user.setBalance(666.6);
        user.setAvatarData(new byte[]{1, 2, 3});
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                .post("/registerAndLogIn/register")
                .content(objectMapper.writeValueAsString(user));
        super.buildRequest(() -> requestBuilder1)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
        user.setPhoneNumber("15555555555");
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                .post("/registerAndLogIn/register")
                .content(objectMapper.writeValueAsString(user));
        super.buildRequest(() -> requestBuilder2)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testLogIn() throws Exception {
        User user = new User();
        user.setUserName("买家");
        user.setPhoneNumber("15177516666");
        user.setUserPassword("123456");
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                .post("/registerAndLogIn/logIn")
                .content(objectMapper.writeValueAsString(user));
        super.buildRequest(() -> requestBuilder1)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        user.setUserPassword("333333");
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                .post("/registerAndLogIn/logIn")
                .content(objectMapper.writeValueAsString(user));
        super.buildRequest(() -> requestBuilder2)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
    }

    @Override
    public String getBaseHeader() {
        return null;
    }
}
