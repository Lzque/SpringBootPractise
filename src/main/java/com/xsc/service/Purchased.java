package com.xsc.service;

import com.xsc.domain.BuyBook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
public interface Purchased {
    List<BuyBook> purchasedBookLoad(Integer userId);

    Boolean comparisonPaymentPassword(String paymentPassword, Integer userId);

    void confirmationReceipt(Integer id);

    void cancelOrder(Integer id);

    void submitUserComment(Integer id, Integer userRating, String userComment, List<MultipartFile> bookCommentImage);
}
