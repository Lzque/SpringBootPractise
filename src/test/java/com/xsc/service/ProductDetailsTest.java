package com.xsc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.SpringBootUnitTest;
import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.Shopping;
import com.xsc.mapper.ShoppingMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class ProductDetailsTest extends SpringBootUnitTest {
    @Autowired
    private ProductDetails productDetails;
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Test
    void testBookDataLoad() {
        Book book = productDetails.bookDataLoad(bookId);
        Assertions.assertNotNull(book);
    }

    @Test
    void testShoppingQuantity() {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        Shopping shoppingQuantity = productDetails.shoppingQuantity(shopping);
        if (shoppingQuantity != null) Assertions.assertTrue(shoppingQuantity.getShoppingQuantity() > -1);
    }

    @Test
    void testAddShoppingCart() {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(1);
        Shopping shoppingOne = productDetails.shoppingQuantity(shopping);// 顺序不能换
        productDetails.addShoppingCart(shopping);
        LambdaQueryWrapper<Shopping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shopping::getUserId, buyerUserId).eq(Shopping::getBookId, bookId);
        Shopping shoppingOneEnd = shoppingMapper.selectOne(queryWrapper);
        if (shoppingOne != null) {
            Assertions.assertEquals(shoppingOne.getShoppingQuantity() + 1, shoppingOneEnd.getShoppingQuantity());
        } else {
            Assertions.assertEquals(1, shoppingOneEnd.getShoppingQuantity());
        }
    }

    @Test
    void testCommentDrawerLoad() {
        List<BuyBook> buyBooks = productDetails.commentDrawerLoad(bookId);
        Assertions.assertEquals(buyBooks.size(),1);
    }
}
