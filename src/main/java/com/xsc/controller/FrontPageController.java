package com.xsc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xsc.domain.*;
import com.xsc.exception.SystemException;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.FrontPage;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/frontPage")
public class FrontPageController {
    @Autowired
    private FrontPage frontPage;
    @Autowired
    private RegisterAndLogInController logInController;

    @GetMapping()
    public Result userDataLoad(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if (userId == null) return new Result(Code.ERR, "用户数据丢失，请重新登录");
        User user = frontPage.userDataById(userId);
        user.setAvatarImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(user.getAvatarData()));
        return new Result(user, Code.OK);
    }

    @PostMapping("/imageBase64")
    public Result ImageBase64(@RequestParam("file") MultipartFile file) {
        return logInController.ImageBase64(file);
    }

    @PutMapping
    public Result updateUserData(@RequestBody User user) {
        // 乐观锁后续再添加，或者加一个在线状态
        boolean updateUser = frontPage.updateUserData(user);
        if (updateUser) {
            return new Result(Code.OK, "信息修改成功");
        } else {
            User userDataById = frontPage.userDataById(user.getId());
            return new Result(userDataById, Code.ERR, "抱歉，信息修改失败");
        }
    }

    @PostMapping
    public Result addToBookData(@RequestBody Book book) {
        // 后面可以的话对于变成商户要更改 user 表
        Integer bookId = frontPage.addToBookData(book);
        return new Result(bookId, Code.OK, "图书信息添加成功");
    }

    @PostMapping("/addBookImage/{bookId}")
    public Result addBookImage(@RequestParam("multipartFile") List<MultipartFile> bookImageFile, @PathVariable Integer bookId) {
        BookImage bookImage = new BookImage();
        bookImage.setBookId(bookId);
        List<byte[]> bytes = new ArrayList<>();
        for (MultipartFile multipartFile : bookImageFile) {
            try {
                // 图片压缩
                bytes.add(ImageHandle.imageCompression(multipartFile.getBytes()));
            } catch (IOException e) {
                throw new SystemException(Code.SYSTEM_ERR, "图片上传时数据丢失，请到售卖管理中重新上传", e);
            }
        }
        // 添加一个图书封面上传
        frontPage.addBookCover(bytes.get(0), bookId);

        for (byte[] aByte : bytes) {
            bookImage.setImage(aByte);
            frontPage.addBookImage(bookImage);
        }
        return new Result(Code.OK, "图书图片上传成功");
    }

    @PostMapping("/{pageNumber}/{stepSize}")
    public Result bookDataLoad(@PathVariable Integer pageNumber, @PathVariable Integer stepSize, @RequestBody SelectBookCondition condition) {
        IPage<Book> bookIPage = frontPage.bookDataLoad(pageNumber, stepSize, condition);
        return new Result(bookIPage, Code.OK);
    }

    @GetMapping("/selectBookRemaining/{bookId}")
    public Result selectBookRemaining(@PathVariable Integer bookId) {
        Integer bookRemaining = frontPage.selectBookRemaining(bookId);
        return new Result(bookRemaining, Code.OK);
    }

    @PostMapping("/shoppingQuantity")
    public Result ShoppingQuantity(@RequestBody Shopping shopping) {
        Shopping shoppingOne = frontPage.shoppingQuantity(shopping);
        Integer shoppingQuantity = shoppingOne != null ? shoppingOne.getShoppingQuantity() : 0;
        return new Result(shoppingQuantity, Code.OK);
    }

    @PostMapping("/addShoppingCar")
    public Result addShoppingCar(@RequestBody Shopping shopping) {
        frontPage.addShoppingCar(shopping);
        return new Result(Code.OK, "加入购物车成功");
    }
}
