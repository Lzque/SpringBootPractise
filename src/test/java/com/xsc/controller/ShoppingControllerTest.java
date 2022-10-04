package com.xsc.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.Shopping;
import com.xsc.domain.User;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

public class ShoppingControllerTest extends ControllerBaseTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBookAddOnLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/shopping")
                .sessionAttr("id", buyerUserId);
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        LinkedHashMap<?, ?> linkedHashMap = objectMapper.readValue(contentAsString, LinkedHashMap.class);
        Object data = linkedHashMap.get("data");
        List<Shopping> shoppingList = objectMapper.convertValue(data, new TypeReference<List<Shopping>>() {
        });
        Assertions.assertEquals(1, shoppingList.size());
        Assertions.assertEquals(3, shoppingList.get(0).getShoppingQuantity());
        Assertions.assertEquals("测试书名", shoppingList.get(0).getBook().getBookName());
    }

    @Test
    void testDeleteAddOnBook() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/shopping/" + shoppingId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        Shopping shopping = shoppingMapper.selectById(shoppingId);
        Assertions.assertNull(shopping);
    }

    @Test
    void testDeleteSelectedBook() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/shopping/deleteSelectedBook")
                .content(objectMapper.writeValueAsString(List.of(shoppingId)));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testUpdateShoppingQuantity() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/shopping/" + shoppingId + "/" + 1);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        Shopping shopping = shoppingMapper.selectById(shoppingId);
        Assertions.assertEquals(1, shopping.getShoppingQuantity());
    }

    @Test
    void testPurchaseBook() throws Exception {
        User userStart = userMapper.selectById(buyerUserId);
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(3);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/shopping/purchaseBook/" + 0 + "/" + 99.9)
                .content(objectMapper.writeValueAsString(List.of(shopping)));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        User userEnd = userMapper.selectById(buyerUserId);
        Assertions.assertEquals(userStart.getBalance(), userEnd.getBalance() + 99.9);
    }

    @Test
    void testComparePassword() throws Exception {
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                .get("/shopping/comparePassword/123456")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder1)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                .get("/shopping/comparePassword/396285")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder2)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
    }

    @Test
    void testAddressConfirmation() throws Exception {
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                .get("/shopping/addressConfirmation")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder1)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, buyerUserId).set(User::getAddress, "广西");
        userMapper.update(null, updateWrapper);
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                .get("/shopping/addressConfirmation")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder2)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Override
    public String getBaseHeader() {
        return null;
    }
}
