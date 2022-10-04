package com.xsc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.Book;
import com.xsc.domain.SelectBookCondition;
import com.xsc.domain.Shopping;
import com.xsc.domain.User;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import com.xsc.protocol.Result;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FrontPageControllerTest extends ControllerBaseTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Test
    void testUserDataLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/frontPage");
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
        requestBuilder.sessionAttr("id", buyerUserId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testUpdateUserData() throws Exception {
        User user = new User();
        user.setUserName("买家更新测试");
        user.setPhoneNumber("15199916666");
        user.setUserPassword("199999");
        user.setPaymentPassword("666666");
        user.setAddress("广西");
        user.setBalance(999.6);
        user.setAvatarData(new byte[]{1, 2, 3, 4, 5, 6});
        user.setId(buyerUserId);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/frontPage")
                .content(new ObjectMapper().writeValueAsString(user));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        User selectByIdUser = userMapper.selectById(buyerUserId);
        Assertions.assertEquals(user.getUserName(), selectByIdUser.getUserName());
        Assertions.assertEquals(user.getPhoneNumber(), selectByIdUser.getPhoneNumber());
        Assertions.assertEquals(user.getUserPassword(), selectByIdUser.getUserPassword());
        Assertions.assertEquals(user.getPaymentPassword(), selectByIdUser.getPaymentPassword());
        Assertions.assertEquals(user.getAddress(), selectByIdUser.getAddress());
        Assertions.assertEquals(user.getBalance(), selectByIdUser.getBalance());
        Assertions.assertArrayEquals(user.getAvatarData(), selectByIdUser.getAvatarData());
    }

    @Test
    void testAddToBookData() throws Exception {
        Book book = new Book();
        book.setBookName("测试书名添加");
        book.setAuthor("测试作者添加");
        book.setPublishingHouse("测试出版社添加");
        book.setSort("测试类别添加");
        book.setPrice(99.9);
        book.setUserId(sellerUserId);
        book.setInStock(99);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/frontPage")
                .content(new ObjectMapper().writeValueAsString(book));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }


    @Test
    void testBookDataLoad() throws Exception {
        SelectBookCondition condition = new SelectBookCondition();
        condition.setBookName("");
        condition.setSort("");
        condition.setOnlyInStock(true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/frontPage/" + 1 + "/" + "6")
                .content(new ObjectMapper().writeValueAsString(condition));
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString();
       /* Result result = new ObjectMapper().readValue(contentAsString, Result.class);
        IPage<?> dataIPage = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(result.getData()), IPage.class);
        List<?> bookList = dataIPage.getRecords();
        for (Object o : bookList) {
            Book book= (Book) o;
            System.out.println(book.getBookName());
        }*/
    }

    @Test
    void testSelectBookRemaining() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/frontPage/selectBookRemaining/" + bookId);
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Result result = new ObjectMapper().readValue(contentAsString, Result.class);
        Assertions.assertEquals(3, result.getData());
    }

    @Test
    void testShoppingQuantity() throws Exception {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/frontPage/shoppingQuantity")
                .content(new ObjectMapper().writeValueAsString(shopping));
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Result result = new ObjectMapper().readValue(contentAsString, Result.class);
        Assertions.assertEquals(3, result.getData());
    }

    @Test
    void testAddShoppingCar() throws Exception {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(3);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/frontPage/addShoppingCar")
                .content(new ObjectMapper().writeValueAsString(shopping));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        LambdaQueryWrapper<Shopping> shoppingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingLambdaQueryWrapper.eq(Shopping::getUserId, buyerUserId).eq(Shopping::getBookId, bookId);
        Shopping selectOne = shoppingMapper.selectOne(shoppingLambdaQueryWrapper);
        Assertions.assertEquals(6, selectOne.getShoppingQuantity());
    }

    @Override
    public String getBaseHeader() {
        return null;
    }
}
