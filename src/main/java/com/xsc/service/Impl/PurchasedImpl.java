package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.CommentImage;
import com.xsc.domain.User;
import com.xsc.exception.SystemException;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.BuyBookMapper;
import com.xsc.mapper.CommentImageMapper;
import com.xsc.mapper.UserMapper;
import com.xsc.protocol.Code;
import com.xsc.service.Purchased;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class PurchasedImpl implements Purchased {
    @Autowired
    private CommentImageMapper commentImageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BuyBookMapper buyBookMapper;

    @Override
    public List<BuyBook> purchasedBookLoad(Integer userId) {
        LambdaQueryWrapper<BuyBook> buyBookQueryWrapper = new LambdaQueryWrapper<>();
        buyBookQueryWrapper.eq(BuyBook::getUserId, userId).orderByDesc(BuyBook::getId);
        List<BuyBook> buyBooks = buyBookMapper.selectList(buyBookQueryWrapper);
        for (BuyBook buyBook : buyBooks) {
            Book book = bookMapper.selectById(buyBook.getBookId());
            book.setImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(book.getCoverData()));
            buyBook.setBook(book);
        }
        return buyBooks;
    }

    @Override
    public Boolean comparisonPaymentPassword(String paymentPassword, Integer userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId).eq(User::getPaymentPassword, paymentPassword);
        Integer integer = userMapper.selectCount(wrapper);
        return integer > 0;
    }

    @Override
    public void confirmationReceipt(Integer id) {
        LambdaUpdateWrapper<BuyBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BuyBook::getId, id).set(BuyBook::getReceipt, true);
        buyBookMapper.update(null, wrapper);
        // 把钱汇到卖家手上，并抽成5%(抽成后面再看看，添加一条默认平台账户记录，配送费一起)
        double userIncome = calculateTotalPrice(id) * 0.95;
        BuyBook buyBook = buyBookMapper.selectById(id);
        LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bookLambdaQueryWrapper.eq(Book::getId, buyBook.getBookId()).select(Book::getUserId);
        Book book = bookMapper.selectOne(bookLambdaQueryWrapper);
        LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<>();
        userWrapper.eq(User::getId, book.getUserId()).setSql("balance=balance+" + userIncome);
        userMapper.update(null, userWrapper);
    }

    @Override
    public void cancelOrder(Integer id) {
        // 退款，先计算订单金额，再看是否有配送费
        BuyBook buyBook = buyBookMapper.selectById(id);
        double refund = calculateTotalPrice(id);
        if (buyBook.getDelivery()) refund += 1;// 每条记录配送费

        LambdaUpdateWrapper<User> userUpdateWrapper = new LambdaUpdateWrapper<>();
        userUpdateWrapper.eq(User::getId, buyBook.getUserId()).setSql("balance=balance+" + refund);
        userMapper.update(null, userUpdateWrapper);
        // 图书表销量减少
        LambdaUpdateWrapper<Book> bookUpdateWrapper = new LambdaUpdateWrapper<>();
        bookUpdateWrapper.eq(Book::getId, buyBook.getBookId()).setSql("sales=sales-" + buyBook.getPurchaseQuantity());
        bookMapper.update(null, bookUpdateWrapper);
        // 购书表删除记录
        buyBookMapper.deleteById(id);
    }

    public double calculateTotalPrice(Integer id) {
        BuyBook buyBook = buyBookMapper.selectById(id);
        LambdaQueryWrapper<Book> bookQueryWrapper = new LambdaQueryWrapper<>();
        bookQueryWrapper.eq(Book::getId, buyBook.getBookId()).select(Book::getPrice);
        Book book = bookMapper.selectOne(bookQueryWrapper);
        return book.getPrice() * buyBook.getPurchaseQuantity();
    }

    @Override
    public void submitUserComment(Integer id, Integer userRating, String userComment, List<MultipartFile> bookCommentImage) {
        LambdaUpdateWrapper<BuyBook> buyBookUpdateWrapper = new LambdaUpdateWrapper<>();
        userComment = userComment.trim();
        buyBookUpdateWrapper.eq(BuyBook::getId, id).set(BuyBook::getRating, userRating)
                .set(userComment != null && !userComment.equals(""), BuyBook::getComment, userComment)
                .set(BuyBook::getCommentStatus, true);
        buyBookMapper.update(null, buyBookUpdateWrapper);
        List<byte[]> bookCommentImageBytes = new ArrayList<>();
        for (MultipartFile multipartFile : bookCommentImage) {
            byte[] bytes = null;
            try {
                bytes = multipartFile.getBytes();
            } catch (IOException e) {
                throw new SystemException(Code.SYSTEM_ERR, "图片上传时数据丢失，抱歉", e);
            }
            bookCommentImageBytes.add(ImageHandle.imageCompression(bytes));
        }
        for (byte[] bookCommentImageByte : bookCommentImageBytes) {
            CommentImage commentImage = new CommentImage();
            commentImage.setBuyBookId(id);
            commentImage.setImage(bookCommentImageByte);
            commentImageMapper.insert(commentImage);
        }
        // 添加更新book表评分
        LambdaQueryWrapper<BuyBook> buyBookQueryWrapper = new LambdaQueryWrapper<>();
        buyBookQueryWrapper.eq(BuyBook::getId, id).select(BuyBook::getBookId);
        BuyBook buyBook = buyBookMapper.selectOne(buyBookQueryWrapper);
        LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bookLambdaQueryWrapper.eq(Book::getId, buyBook.getBookId()).select(Book::getOverallRating);
        Book book = bookMapper.selectOne(bookLambdaQueryWrapper);
        double overallRating = (Math.round((book.getOverallRating() + userRating) * 10.0 / 2.0) / 10.0);
        LambdaUpdateWrapper<Book> bookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        bookLambdaUpdateWrapper.eq(Book::getId, buyBook.getBookId()).set(Book::getOverallRating, overallRating);
        bookMapper.update(null, bookLambdaUpdateWrapper);
    }
}
