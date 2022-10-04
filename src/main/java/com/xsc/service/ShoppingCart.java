package com.xsc.service;

import com.xsc.domain.Shopping;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ShoppingCart {
    List<Shopping> bookAddOnLoad(Integer userId);

    void deleteAddOnBook(Integer id);

    void deleteSelectedBook(List<Integer> addOnIds);

    void updateShoppingQuantity(Integer id, Integer newShoppingQuantity);

    String purchaseBook(List<Shopping> shoppingList, Boolean deliveryMethod, Double bookPrice);

    Boolean comparePassword(String enterPaymentPassword, Integer userId);

    Boolean addressConfirmation(Integer userId);
}
