package com.xsc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.SpringBootUnitTest;
import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.CommentImage;
import com.xsc.domain.User;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.BuyBookMapper;
import com.xsc.mapper.CommentImageMapper;
import com.xsc.mapper.UserMapper;
import com.xsc.utils.ImageHandle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PurchasedTest extends SpringBootUnitTest {
    @Autowired
    private Purchased purchased;
    @Autowired
    private BuyBookMapper buyBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private CommentImageMapper commentImageMapper;

    @Test
    void testPurchasedBookLoad() {
        List<BuyBook> buyBooks = purchased.purchasedBookLoad(buyerUserId);
        Assertions.assertNotNull(buyBooks);
    }

    @Test
    void testComparisonPaymentPassword() {
        Boolean comparisonPaymentPassword = purchased.comparisonPaymentPassword("123666999", buyerUserId);
        Assertions.assertFalse(comparisonPaymentPassword);
    }

    @Test
    void testConfirmationReceipt() {
        BuyBook buyBook = buyBookMapper.selectById(buyBookId);
        Book book = bookMapper.selectById(buyBook.getBookId());
        User userStart = userMapper.selectById(book.getUserId());
        purchased.confirmationReceipt(buyBookId);
        User userEnd = userMapper.selectById(book.getUserId());
        Assertions.assertTrue(userStart.getBalance() < userEnd.getBalance());
    }

    @Test
    void testCancelOrder() {
        BuyBook buyBook = buyBookMapper.selectById(buyBookId);
        User userStart = userMapper.selectById(buyBook.getUserId());
        purchased.cancelOrder(buyBookId);
        User userEnd = userMapper.selectById(buyBook.getUserId());
        Assertions.assertTrue(userStart.getBalance() < userEnd.getBalance());
    }

    @Test
    void testSubmitUserComment() throws IOException {
        purchased.submitUserComment(buyBookId, 3, "这是测试评论getComment", List.of(multipartFile, multipartFile));
        BuyBook buyBook = buyBookMapper.selectById(buyBookId);
        Assertions.assertEquals(buyBook.getRating(), 3);
        Assertions.assertEquals(buyBook.getComment(), "这是测试评论getComment");
        LambdaQueryWrapper<CommentImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentImage::getBuyBookId, buyBookId);
        byte[] multipartFileBytes = ImageHandle.imageCompression(multipartFile.getBytes());
        List<CommentImage> commentImages = commentImageMapper.selectList(queryWrapper);
        for (CommentImage commentImage : commentImages) {
            if (Arrays.equals(commentImage.getImage(), new byte[]{1, 2, 3})) continue;
            Assertions.assertArrayEquals(multipartFileBytes, commentImage.getImage());
        }
    }
}
