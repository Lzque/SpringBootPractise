package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xsc.domain.*;
import com.xsc.mapper.*;
import com.xsc.service.FrontPage;
import com.xsc.service.ProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProductDetailsImpl implements ProductDetails {
    @Autowired
    private CommentImageMapper commentImageMapper;
    @Autowired
    private FrontPage frontPage;
    @Autowired
    private BuyBookMapper buyBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookImageMapper bookImageMapper;

    @Override
    public Book bookDataLoad(Integer id) {
        // book 基本信息
        Book book = bookMapper.selectById(id);
        LambdaQueryWrapper<BookImage> bookImageWrapper = new LambdaQueryWrapper<>();
        bookImageWrapper.eq(BookImage::getBookId, id);
        List<BookImage> bookImages = bookImageMapper.selectList(bookImageWrapper);
        List<String> imageBase64s = new ArrayList<>();
        for (BookImage bookImage : bookImages) {
            String bookImageBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(bookImage.getImage());
            imageBase64s.add(bookImageBase64);
        }
        book.setBookImageBase64s(imageBase64s);
        // book 卖家信息
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getId, book.getUserId()).select(User::getUserName);
        User user = userMapper.selectOne(userQueryWrapper);
        book.setSellerUserName(user.getUserName());
        // 图书评论数
        LambdaQueryWrapper<BuyBook> buyBookQueryWrapper = new LambdaQueryWrapper<>();
        buyBookQueryWrapper.eq(BuyBook::getBookId, id).select(BuyBook::getCommentStatus);
        List<BuyBook> buyBooks = buyBookMapper.selectList(buyBookQueryWrapper);
        int commentQuantity = 0;
        for (BuyBook buyBook : buyBooks) {
            if (buyBook.getCommentStatus()) commentQuantity += 1;
        }
        book.setCommentQuantity(commentQuantity);
        return book;
    }

    @Override
    public Shopping shoppingQuantity(Shopping shopping) {
        return frontPage.shoppingQuantity(shopping);

    }

    @Override
    public void addShoppingCart(Shopping shopping) {
        frontPage.addShoppingCar(shopping);
    }

    @Override
    public List<BuyBook> commentDrawerLoad(Integer bookId) {
        LambdaQueryWrapper<BuyBook> buyBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        buyBookLambdaQueryWrapper.eq(BuyBook::getBookId, bookId).eq(BuyBook::getCommentStatus, true);
        List<BuyBook> buyBooks = buyBookMapper.selectList(buyBookLambdaQueryWrapper);
        for (BuyBook buyBook : buyBooks) {
            // 评论图片
            LambdaQueryWrapper<CommentImage> commentImageQuery = new LambdaQueryWrapper<>();
            commentImageQuery.eq(CommentImage::getBuyBookId, buyBook.getId());
            List<CommentImage> commentImages = commentImageMapper.selectList(commentImageQuery);
            List<String> images = new ArrayList<>();
            for (CommentImage commentImage : commentImages) {
                images.add("data:image/png;base64," + Base64.getEncoder().encodeToString(commentImage.getImage()));
            }
            buyBook.setCommentImageBase64s(images);
            // 用户名
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, buyBook.getUserId()).select(User::getUserName, User::getAvatarData);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            user.setAvatarImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(user.getAvatarData()));
            buyBook.setUser(user);
        }
        return buyBooks;
    }
}
