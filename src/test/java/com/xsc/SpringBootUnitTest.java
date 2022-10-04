package com.xsc;

import com.xsc.domain.*;
import com.xsc.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@Transactional
@Rollback
public class SpringBootUnitTest {
    // 作为后续测试上传的图片文件
    public static MultipartFile multipartFile;

    static {
        File file = new File("src\\main\\resources\\static\\image\\watermark.png");
        try {
            multipartFile = new MockMultipartFile(
                    file.getName(), //文件名
                    new FileInputStream(file) //文件流
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private BookImageMapper bookImageMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BuyBookMapper buyBookMapper;
    @Autowired
    private CommentImageMapper commentImageMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;
    @Autowired
    private UserMapper userMapper;
    // 和后续的测试方法共享数据
    public static Integer sellerUserId;
    public static Integer buyerUserId;
    public static Integer bookId;
    public static Integer bookImageId;
    public static Integer buyBookId;
    public static Integer commentImageId;
    public static Integer shoppingId;

    @BeforeEach
    public void prepareDatabaseRecordInstance() {
        // 用户表
        User user = new User();
        user.setUserName("买家");
        user.setPhoneNumber("15177516666");
        user.setUserPassword("123456");
        user.setPaymentPassword("123456");
        user.setAddress("无");
        user.setWallet(true);
        user.setBalance(666.6);
        user.setAvatarData(new byte[]{1, 2, 3});
        userMapper.insert(user);
        buyerUserId = user.getId();

        user.setUserName("卖家");
        user.setPhoneNumber("15177511999");
        user.setUserPassword("123456");
        user.setWallet(true);
        user.setBalance(0.0);
        user.setAvatarData(new byte[]{1, 2, 3});
        userMapper.insert(user);
        sellerUserId = user.getId();
        // 图书表
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
        bookMapper.insert(book);
        bookId = book.getId();
        // 图书图片表
        BookImage bookImage = new BookImage();
        bookImage.setBookId(bookId);
        bookImage.setImage(new byte[]{1, 2, 3});
        bookImageMapper.insert(bookImage);
        bookImageId = bookImage.getId();
        // 用户加购表
        Shopping shopping = new Shopping();
        shopping.setUserId(buyerUserId);
        shopping.setBookId(bookId);
        shopping.setShoppingQuantity(3);
        shoppingMapper.insert(shopping);
        shoppingId = shopping.getId();
        // 用户购书表
        BuyBook buyBook = new BuyBook();
        buyBook.setUserId(buyerUserId);
        buyBook.setBookId(bookId);
        buyBook.setPurchaseQuantity(6);
        buyBook.setReceipt(true);
        buyBook.setCommentStatus(true);
        buyBook.setComment("该用户暂无评论lalala");
        buyBookMapper.insert(buyBook);
        buyBookId = buyBook.getId();
        // 评论图片表
        CommentImage commentImage = new CommentImage();
        commentImage.setBuyBookId(buyBookId);
        commentImage.setImage(new byte[]{1, 2, 3});
        commentImageMapper.insert(commentImage);
        commentImageId = commentImage.getId();
    }
}












