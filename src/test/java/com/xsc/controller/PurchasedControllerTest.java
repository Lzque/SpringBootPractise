package com.xsc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.BuyBook;
import com.xsc.mapper.BuyBookMapper;
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

public class PurchasedControllerTest extends ControllerBaseTest {
    @Autowired
    private BuyBookMapper buyBookMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPurchasedBookLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/purchased")
                .sessionAttr("id", buyerUserId);
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);// 乱码解决
        LinkedHashMap<?, ?> linkedHashMap = objectMapper.readValue(contentAsString, LinkedHashMap.class);
        Object data = linkedHashMap.get("data");
        List<BuyBook> buyBooks = objectMapper.convertValue(data, new TypeReference<List<BuyBook>>() {
        });
        Assertions.assertEquals(1, buyBooks.size());
        Assertions.assertEquals(true, buyBooks.get(0).getReceipt());
    }

    @Test
    void testComparisonPaymentPassword() throws Exception {
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                .get("/purchased/comparisonPaymentPassword/" + "123456")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder1)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                .get("/purchased/comparisonPaymentPassword/" + "654321")
                .sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder2)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
    }

    @Test
    void testConfirmationReceipt() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/purchased/confirmationReceipt/" + buyBookId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));// ~.~
    }

    @Test
    void testCancelOrder() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/purchased/cancelOrder/" + buyBookId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        BuyBook buyBook = buyBookMapper.selectById(buyBookId);
        Assertions.assertNull(buyBook);
    }



    @Override
    public String getBaseHeader() {
        return null;
    }
}
