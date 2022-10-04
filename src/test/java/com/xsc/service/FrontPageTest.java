package com.xsc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xsc.SpringBootUnitTest;
import com.xsc.domain.*;
import com.xsc.mapper.BookImageMapper;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FrontPageTest extends SpringBootUnitTest {
    @Autowired
    private FrontPage frontPage;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookImageMapper bookImageMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Test
    void testUserDataById() {
        User user = frontPage.userDataById(buyerUserId);
        Assertions.assertNotNull(user);
    }

    @Test
    void testUpdateUserData() {
        User user = new User();
        user.setId(buyerUserId);
        user.setUserName("这是一个更改用户名的测试");
        frontPage.updateUserData(user);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, buyerUserId);
        User selectOneUser = userMapper.selectOne(queryWrapper);
        Assertions.assertEquals(selectOneUser.getUserName(), user.getUserName());
    }

    @Test
    void testAddToBookData() {
        Book book = new Book();
        book.setBookName("测试添加Book数据");
        book.setAuthor("测试添加Book数据");
        book.setPublishingHouse("测试添加Book数据");
        book.setSort("测试添加Book数据");
        book.setPrice(99.9);
        book.setUserId(sellerUserId);
        Integer selectCountStart = bookMapper.selectCount(null);
        frontPage.addToBookData(book);
        Integer selectCountEnd = bookMapper.selectCount(null);
        Assertions.assertEquals(selectCountStart + 1, selectCountEnd);
    }

    @Test
    void testAddBookImage() {
        byte[] bytes = {1, 2, 3};
        BookImage bookImage = new BookImage();
        bookImage.setImage(bytes);
        bookImage.setBookId(bookId);
        Integer selectCountStart = bookImageMapper.selectCount(null);
        frontPage.addBookImage(bookImage);
        Integer selectCountEnd = bookImageMapper.selectCount(null);
        Assertions.assertEquals(selectCountStart + 1, selectCountEnd);
    }

    @Test
    void testAddBookCover() {
        byte[] bytes = {1, 2, 3};
        frontPage.addBookCover(bytes, bookId);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getId, bookId);
        Book book = bookMapper.selectOne(queryWrapper);
        Assertions.assertArrayEquals(book.getCoverData(), bytes);
    }

    @Test
    void testBookDataLoad() {// 这里全部测试组合有点多，先测试一个有货
        SelectBookCondition condition = new SelectBookCondition();
        condition.setOnlyInStock(true);
        condition.setBookName("");
        condition.setSort("");
        IPage<Book> page = frontPage.bookDataLoad(1, 6, condition);
        List<Book> bookList = page.getRecords();
        boolean bool = true;
        for (Book book : bookList) {
            bool = bool && book.getInStock() - book.getSales() > 0;
        }
        Assertions.assertTrue(bool);
    }

    @Test
    void testSelectBookRemaining() {
        Integer bookRemaining = frontPage.selectBookRemaining(bookId);
        Assertions.assertTrue(bookRemaining > -1);
    }

    @Test
    void testShoppingQuantity() {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        Shopping shoppingQuantity = frontPage.shoppingQuantity(shopping);
        if (shoppingQuantity != null) Assertions.assertTrue(shoppingQuantity.getShoppingQuantity() > -1);
    }

    @Test
    void testAddShoppingCar() {
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(1);
        Shopping shoppingOne = frontPage.shoppingQuantity(shopping);// 顺序不能换
        frontPage.addShoppingCar(shopping);
        LambdaQueryWrapper<Shopping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shopping::getUserId, buyerUserId).eq(Shopping::getBookId, bookId);
        Shopping shoppingOneEnd = shoppingMapper.selectOne(queryWrapper);
        if (shoppingOne != null) {
            Assertions.assertEquals(shoppingOne.getShoppingQuantity() + 1, shoppingOneEnd.getShoppingQuantity());
        } else {
            Assertions.assertEquals(1, shoppingOneEnd.getShoppingQuantity());
        }
    }
}
