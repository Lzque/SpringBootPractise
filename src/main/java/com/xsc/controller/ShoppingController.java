package com.xsc.controller;

import com.xsc.domain.Shopping;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {
    @Autowired
    private ShoppingCart shoppingCart;

    @GetMapping()
    public Result bookAddOnLoad(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if (userId == null) return new Result(Code.ERR, "用户数据丢失，请重新登录");
        List<Shopping> shoppingList = shoppingCart.bookAddOnLoad(userId);
        return new Result(shoppingList, Code.OK);
    }

    @DeleteMapping("/{id}")
    public Result deleteAddOnBook(@PathVariable Integer id) {
        shoppingCart.deleteAddOnBook(id);
        return new Result(Code.OK, "删除成功");
    }

    @PostMapping("/deleteSelectedBook")
    public Result deleteSelectedBook(@RequestBody List<Integer> addOnIds) {
        shoppingCart.deleteSelectedBook(addOnIds);
        return new Result(Code.OK, "删除成功");
    }

    @PutMapping("/{id}/{newShoppingQuantity}")
    public Result updateShoppingQuantity(@PathVariable Integer id, @PathVariable Integer newShoppingQuantity) {
        shoppingCart.updateShoppingQuantity(id, newShoppingQuantity);
        return new Result(Code.OK, "更改成功");
    }

    @PostMapping("/purchaseBook/{deliveryMethod}/{bookPrice}")
    public Result purchaseBook(@RequestBody List<Shopping> shoppingList, @PathVariable Boolean deliveryMethod, @PathVariable Double bookPrice) {
        String purchaseBookMessage = shoppingCart.purchaseBook(shoppingList, deliveryMethod, bookPrice);
        return new Result(Code.OK, purchaseBookMessage);
    }

    @GetMapping("/comparePassword/{enterPaymentPassword}")
    public Result comparePassword(@PathVariable String enterPaymentPassword, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        Boolean comparePassword = shoppingCart.comparePassword(enterPaymentPassword, userId);
        if (comparePassword) {
            return new Result(Code.OK, "支付密码输入正确");
        } else {
            return new Result(Code.ERR, "支付密码输入错误");
        }
    }

    @GetMapping("/addressConfirmation")
    public Result addressConfirmation(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if (shoppingCart.addressConfirmation(userId)) {
            return new Result(Code.OK, "地址存在");
        } else {
            return new Result(Code.ERR, "地址未完善，请到个人信息管理中完善");
        }
    }
}
