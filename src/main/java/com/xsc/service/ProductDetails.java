package com.xsc.service;

import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.Shopping;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProductDetails {
    Book bookDataLoad(Integer id);

    Shopping shoppingQuantity(Shopping shopping);

    void addShoppingCart(Shopping shopping);

    List<BuyBook> commentDrawerLoad(Integer bookId);
}
