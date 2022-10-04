package com.xsc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.SpringBootUnitTest;
import com.xsc.domain.Book;
import com.xsc.domain.Shopping;
import com.xsc.domain.User;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShoppingCartTest extends SpringBootUnitTest {
    @Autowired
    private ShoppingCart shoppingCart;
    @Autowired
    private ShoppingMapper shoppingMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;

    @Test
    void testBookAddOnLoad() {
        List<Shopping> shoppingList = shoppingCart.bookAddOnLoad(buyerUserId);
        Assertions.assertEquals(1, shoppingList.size());
    }

    @Test
    void testDeleteAddOnBook() {
        shoppingCart.deleteAddOnBook(shoppingId);
        LambdaQueryWrapper<Shopping> shoppingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingLambdaQueryWrapper.eq(Shopping::getUserId, buyerUserId);
        Integer integer = shoppingMapper.selectCount(shoppingLambdaQueryWrapper);
        Assertions.assertEquals(0, integer);
    }

    @Test
    void testDeleteSelectedBook() {
        shoppingCart.deleteSelectedBook(List.of(shoppingId));
        LambdaQueryWrapper<Shopping> shoppingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingLambdaQueryWrapper.eq(Shopping::getUserId, buyerUserId);
        Integer integer = shoppingMapper.selectCount(shoppingLambdaQueryWrapper);
        Assertions.assertEquals(0, integer);
    }

    @Test
    void testUpdateShoppingQuantity() {
        shoppingCart.updateShoppingQuantity(shoppingId, 1);
        Shopping shopping = shoppingMapper.selectById(shoppingId);
        Assertions.assertEquals(1, shopping.getShoppingQuantity());
    }

    @Test
    void testPurchaseBook() {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(3);
        User userStart = userMapper.selectById(buyerUserId);
        Book bookStart = bookMapper.selectById(bookId);
        shoppingCart.purchaseBook(List.of(shopping), true, 66.0);
        User userEnd = userMapper.selectById(buyerUserId);
        Book bookEnd = bookMapper.selectById(bookId);
        Assertions.assertEquals(userStart.getBalance() - 66.0, userEnd.getBalance());
        Assertions.assertEquals(bookStart.getSales() + 3, bookEnd.getSales());
    }

    @Test
    void testComparePassword() {
        Boolean comparePassword = shoppingCart.comparePassword("123456", buyerUserId);
        Assertions.assertTrue(comparePassword);
        comparePassword = shoppingCart.comparePassword("6666666", buyerUserId);
        Assertions.assertFalse(comparePassword);
    }

    @Test
    void testAddressConfirmation() {
        Boolean addressConfirmation = shoppingCart.addressConfirmation(buyerUserId);
        Assertions.assertFalse(addressConfirmation);
    }
}
