package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xsc.domain.*;
import com.xsc.mapper.BookImageMapper;
import com.xsc.mapper.BookMapper;
import com.xsc.mapper.ShoppingMapper;
import com.xsc.mapper.UserMapper;
import com.xsc.service.FrontPage;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Base64;
import java.util.List;

@Service
public class FrontPageImpl implements FrontPage {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookImageMapper bookImageMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;

    @Override
    public User userDataById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean updateUserData(User user) {
        int i = userMapper.updateById(user);
        return i > 0;
    }

    @Override
    public Integer addToBookData(Book book) {
        bookMapper.insert(book);
        return book.getId();
    }

    @Override
    public void addBookImage(BookImage bookImage) {
        bookImageMapper.insert(bookImage);
    }

    @Override
    public void addBookCover(byte[] coverData, Integer bookId) {
        LambdaUpdateWrapper<Book> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Book::getId, bookId).set(Book::getCoverData, coverData);
        bookMapper.update(null, wrapper);
    }

    @Override
    public IPage<Book> bookDataLoad(Integer pageNumber, Integer stepSize, SelectBookCondition condition) {
        IPage<Book> page = new Page<>(pageNumber, stepSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!condition.getBookName().equals(""), Book::getBookName, condition.getBookName())
                .between(condition.getPriceStart() != null && condition.getPriceEnd() != null, Book::getPrice, condition.getPriceStart(), condition.getPriceEnd())
                .like(!condition.getSort().equals(""), Book::getSort, condition.getSort());
        IPage<Book> bookIPage = bookMapper.selectPage(page, wrapper);
        // 可以添加一个对于最大页数和传入页数的比较

        List<Book> books = bookIPage.getRecords();
        if (condition.getOnlyInStock() == null) {
            condition.setOnlyInStock(false);
        }
        // 倒着删除否则下标变化，删除不干净
        for (int i = books.size() - 1; i >= 0; i--) {
            Book book = books.get(i);
            if (book.getInStock() - book.getSales() <= 0) {
                if (condition.getOnlyInStock()) {
                    books.remove(book); // 删除，做得不好
                } else {
                    // 售罄加水印
                    book.setCoverData(ImageHandle.addSoldOutWatermark(book.getCoverData()));
                }
            }
        }
        // 不能放到上面，否则图片没了
        for (Book book : books) {
            book.setImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(book.getCoverData()));
        }

        bookIPage.setRecords(books);

        return bookIPage;
    }

    @Override
    public Integer selectBookRemaining(Integer bookId) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getId, bookId)
                .select(Book::getSales, Book::getInStock);
        Book book = bookMapper.selectOne(queryWrapper);
        return book.getInStock() - book.getSales();
    }

    @Override
    public Shopping shoppingQuantity(Shopping shopping) {
        LambdaQueryWrapper<Shopping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shopping::getUserId, shopping.getUserId())
                .eq(Shopping::getBookId, shopping.getBookId())
                .select(Shopping::getShoppingQuantity);
        return shoppingMapper.selectOne(wrapper);

    }

    @Override
    public void addShoppingCar(Shopping shopping) {
        Shopping shoppingOne = shoppingQuantity(shopping);
        if (shoppingOne != null) {
            LambdaUpdateWrapper<Shopping> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Shopping::getUserId, shopping.getUserId())
                    .eq(Shopping::getBookId, shopping.getBookId())
                    .setSql("shopping_quantity=shopping_quantity+" + shopping.getShoppingQuantity());
            shoppingMapper.update(null, updateWrapper);
        } else {
            shoppingMapper.insert(shopping);
        }
    }
}
