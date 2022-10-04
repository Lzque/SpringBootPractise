package com.xsc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.SpringBootUnitTest;
import com.xsc.domain.Book;
import com.xsc.domain.BookImage;
import com.xsc.mapper.BookImageMapper;
import com.xsc.mapper.BookMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

public class BookManageTest extends SpringBootUnitTest {
    @Autowired
    private BookManage bookManage;
    @Autowired
    private BookImageMapper bookImageMapper;
    @Autowired
    private BookMapper bookMapper;

    @Test
    void testBooksByUserId() {
        List<Book> books = bookManage.booksByUserId(sellerUserId);
        Assertions.assertTrue(books.size() > 0);
    }

    @Test
    void testImageByBookId() {
        List<BookImage> bookImages = bookManage.imageByBookId(bookId);
        Assertions.assertTrue(bookImages.size() > 0);
    }

    @Test
    void testReplaceBookImage() {
        byte[] bytes = {1, 2, 3};
        bookManage.replaceBookImage(bookImageId, bytes);
        BookImage bookImage = bookImageMapper.selectById(bookImageId);
        Assertions.assertArrayEquals(bookImage.getImage(), bytes);
    }

    @Test
    void testDeleteBookImage() {
        bookManage.deleteBookImage(bookImageId);
        LambdaQueryWrapper<BookImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookImage::getId, bookImageId);
        Integer selectCount = bookImageMapper.selectCount(queryWrapper);
        Assertions.assertEquals(0, selectCount);
    }

    @Test
    void testAddBookImage() {
        byte[] bytes = {1, 2, 3};
        BookImage bookImage = bookManage.addBookImage(bookId, bytes);
        LambdaQueryWrapper<BookImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookImage::getId, bookImage.getId());
        BookImage selectOne = bookImageMapper.selectOne(queryWrapper);
        Assertions.assertArrayEquals(selectOne.getImage(), bytes);
    }

    @Test
    void testUpdateBook() {
        Book book = new Book();
        book.setId(bookId);
        book.setBookName("这是测试管理图书更新书名");
        bookManage.updateBook(book);
        Book bookById = bookMapper.selectById(bookId);
        Assertions.assertEquals(book.getBookName(), bookById.getBookName());
    }

    @Test
    void testReplaceBookCover() {
        byte[] bytes = {1, 2, 3};
        bookManage.replaceBookCover(bookId, bytes);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getId, bookId);
        Book book = bookMapper.selectOne(queryWrapper);
        Assertions.assertArrayEquals(book.getCoverData(), bytes);
    }
}
