package com.xsc.controller;

import com.xsc.domain.BuyBook;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.Purchased;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/purchased")
public class PurchasedController {
    @Autowired
    private Purchased purchased;

    @GetMapping()
    public Result purchasedBookLoad(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if (userId == null) return new Result(Code.ERR, "用户数据丢失，请重新登录");
        List<BuyBook> buyBooks = purchased.purchasedBookLoad(userId);
        return new Result(buyBooks, Code.OK, "加载完成");
    }

    @GetMapping("/comparisonPaymentPassword/{paymentPassword}")
    public Result comparisonPaymentPassword(@PathVariable String paymentPassword, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        Boolean comparison = purchased.comparisonPaymentPassword(paymentPassword, userId);
        if (comparison) {
            return new Result(Code.OK, "支付密码输入正确");
        } else {
            return new Result(Code.ERR, "支付密码输入错误");
        }
    }

    @PutMapping("/confirmationReceipt/{id}")
    public Result confirmationReceipt(@PathVariable Integer id) {
        purchased.confirmationReceipt(id);
        return new Result(Code.OK, "确认收货成功");
    }

    @DeleteMapping("/cancelOrder/{id}")
    public Result cancelOrder(@PathVariable Integer id) {
        purchased.cancelOrder(id);
        return new Result(Code.OK, "订单取消成功");
    }

    @PostMapping("/submitUserComment/{id}/{userRating}/{userComment}")
    public Result submitUserComment(@PathVariable Integer id,
                                    @PathVariable Integer userRating,
                                    @PathVariable String userComment,
                                    @RequestBody @RequestParam("multipartFile") List<MultipartFile> bookCommentImage) {
        purchased.submitUserComment(id, userRating, userComment, bookCommentImage);
        return new Result(Code.OK, "评价提交成功");
    }
}
