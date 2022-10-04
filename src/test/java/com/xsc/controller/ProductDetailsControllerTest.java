package com.xsc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.Shopping;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.protocol.Result;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

public class ProductDetailsControllerTest extends ControllerBaseTest {
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Test
    void testBookDataLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/productDetails/" + bookId);
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);// 乱码解决
        /*这里要注意*/
        LinkedHashMap<?, ?> result = new ObjectMapper().readValue(contentAsString, LinkedHashMap.class);
        Object data = result.get("data");
        Book resultData = new ObjectMapper().convertValue(data, Book.class);

        Book book = new Book();
        book.setBookName("测试书名");
        book.setAuthor("测试作者");
        book.setPublishingHouse("测试出版社");
        book.setSort("测试类别");
        book.setPrice(99.9);
        book.setUserId(sellerUserId);
        book.setInStock(9);
        book.setSales(6);
        book.setCoverData(new byte[]{1, 2, 3});
        Assertions.assertEquals(book.getBookName(), resultData.getBookName());
        Assertions.assertEquals(book.getAuthor(), resultData.getAuthor());
        Assertions.assertEquals(book.getPublishingHouse(), resultData.getPublishingHouse());
        Assertions.assertEquals(book.getSort(), resultData.getSort());
        Assertions.assertEquals(book.getPrice(), resultData.getPrice());
        Assertions.assertEquals(book.getUserId(), resultData.getUserId());
        Assertions.assertEquals(book.getInStock(), resultData.getInStock());
        Assertions.assertEquals(book.getSales(), resultData.getSales());
        Assertions.assertArrayEquals(book.getCoverData(), resultData.getCoverData());
    }

    @Test
    void testShoppingQuantity() throws Exception {
        Shopping shopping = new Shopping();
        shopping.setBookId(bookId);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/productDetails/shoppingQuantity")
                .sessionAttr("id", buyerUserId)
                .content(new ObjectMapper().writeValueAsString(shopping));
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);// 乱码解决
        LinkedHashMap<?, ?> result = new ObjectMapper().readValue(contentAsString, LinkedHashMap.class);
        Integer shoppingQuantity = (Integer) result.get("data");
        Assertions.assertEquals(3, shoppingQuantity);
    }

    @Test
    void testAddShoppingCart() throws Exception {
        Shopping shopping = new Shopping();
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(3);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/productDetails/addShoppingCart")
                .sessionAttr("id", buyerUserId)
                .content(new ObjectMapper().writeValueAsString(shopping));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        LambdaQueryWrapper<Shopping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shopping::getUserId, buyerUserId).eq(Shopping::getBookId, bookId);
        Shopping selectOne = shoppingMapper.selectOne(queryWrapper);
        Assertions.assertEquals(selectOne.getShoppingQuantity(), 6);
    }

    @Test
    void testCommentDrawerLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/productDetails/commentDrawerLoad/" + bookId);
        String contentAsString = super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        LinkedHashMap<?, ?> result = new ObjectMapper().readValue(contentAsString, LinkedHashMap.class);
        Object data = result.get("data");
        List<BuyBook> buyBooks = new ObjectMapper().convertValue(data, new TypeReference<List<BuyBook>>() {
        });// 转换成功^.^
        /*ObjectMapper mapper = new ObjectMapper();
        POJO pojo = mapper.convertValue(mapObject,  new TypeReference<POJO>() { });
        // or:
        List<POJO> pojos = mapper.convertValue(listmapObjects, new TypeReference<List<POJO>>() { });*/
        BuyBook buyBook = buyBooks.get(0);
        Assertions.assertEquals(buyBook.getComment(), "该用户暂无评论lalala");
    }

    @Override
    public String getBaseHeader() {
        return null;
    }
}
