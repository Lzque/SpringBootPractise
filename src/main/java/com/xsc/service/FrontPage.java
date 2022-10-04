package com.xsc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xsc.domain.*;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FrontPage {
    User userDataById(Integer id);

    boolean updateUserData(User user);

    Integer addToBookData(Book book);

    void addBookImage(BookImage bookImage);

    void addBookCover(byte[] coverData, Integer bookId);

    IPage<Book> bookDataLoad(Integer pageNumber, Integer stepSize, SelectBookCondition condition);

    Integer selectBookRemaining(Integer bookId);

    Shopping shoppingQuantity(Shopping shopping);

    void addShoppingCar(Shopping shopping);
}
