package com.xsc.controller;

import com.xsc.domain.Book;
import com.xsc.domain.BuyBook;
import com.xsc.domain.Shopping;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.ProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/productDetails")
public class ProductDetailsController {

    @Autowired
    private ProductDetails productDetails;

    @GetMapping("/{id}")
    public Result bookDataLoad(@PathVariable Integer id) {
        Book book = productDetails.bookDataLoad(id);
        return new Result(book, Code.OK);
    }

    @PostMapping("/shoppingQuantity")
    public Result shoppingQuantity(@RequestBody Shopping shopping, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if(userId==null) return new Result(Code.ERR,"用户数据丢失，请重新登录");
        shopping.setUserId(userId);
        Shopping shoppingOne = productDetails.shoppingQuantity(shopping);
        Integer shoppingQuantity = shoppingOne != null ? shoppingOne.getShoppingQuantity() : 0;
        return new Result(shoppingQuantity, Code.OK);
    }

    @PostMapping("/addShoppingCart")
    public Result addShoppingCart(@RequestBody Shopping shopping,HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if(userId==null) return new Result(Code.ERR,"用户数据丢失，请重新登录");
        shopping.setUserId(userId);
        productDetails.addShoppingCart(shopping);
        return new Result(Code.OK, "加入购物车成功");
    }

    @GetMapping("/commentDrawerLoad/{bookId}")
    public Result commentDrawerLoad(@PathVariable Integer bookId) {
        List<BuyBook> buyBooks = productDetails.commentDrawerLoad(bookId);
        return new Result(buyBooks, Code.OK);
    }
}
