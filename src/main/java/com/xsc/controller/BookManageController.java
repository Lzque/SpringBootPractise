package com.xsc.controller;

import com.xsc.domain.Book;
import com.xsc.domain.BookImage;
import com.xsc.exception.SystemException;
import com.xsc.protocol.Code;
import com.xsc.protocol.Result;
import com.xsc.service.BookManage;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/bookManage")
public class BookManageController {
    @Autowired
    private BookManage bookManage;

    @DeleteMapping("/takeDownBook/{bookId}")
    public Result takeDownBook(@PathVariable Integer bookId) {
        bookManage.takeDownBook(bookId);
        return new Result(Code.OK, "图书已下架");// 确定是否添加这个功能？
    }

    @GetMapping()
    public Result booksLoad(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("id");
        if (userId == null) return new Result(Code.ERR, "用户数据丢失，请重新登录");
        List<Book> books = bookManage.booksByUserId(userId);
        return new Result(books, Code.OK);
    }

    @GetMapping("/editImageLoad/{bookId}")
    public Result editImageLoad(@PathVariable Integer bookId) {
        List<BookImage> bookImages = bookManage.imageByBookId(bookId);
        return new Result(bookImages, Code.OK);
    }

    @PostMapping("/replaceBookImage/{bookImageId}/{bookImageNumbering}/{bookId}")
    public Result replaceBookImage(@PathVariable Integer bookImageId,
                                   @PathVariable Integer bookImageNumbering,
                                   @PathVariable Integer bookId,
                                   @RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            bytes = ImageHandle.imageCompression(bytes);
            bookManage.replaceBookImage(bookImageId, bytes);
            // 是更改第一张图片，则更新图书封面
            if (bookImageNumbering == 0) {
                bookManage.replaceBookCover(bookId, bytes);
            }
            BookImage bookImage = new BookImage();
            bookImage.setId(bookImageId);
            bookImage.setBase64String("data:image/png;base64," + Base64.getEncoder().encodeToString(bytes));
            return new Result(bookImage, Code.OK, "图片替换成功");
        } catch (IOException e) {
            throw new SystemException(Code.SYSTEM_ERR, "图片替换失败，请重试", e);
        }
    }

    @DeleteMapping("/deleteBookImage/{bookImageId}")
    public Result deleteBookImage(@PathVariable Integer bookImageId) {
        bookManage.deleteBookImage(bookImageId);
        return new Result(Code.OK, "图片删除成功");
    }

    @PostMapping("/addBookImage/{bookId}")
    public Result addBookImage(@PathVariable Integer bookId, @RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            bytes = ImageHandle.imageCompression(bytes);
            BookImage bookImage = bookManage.addBookImage(bookId, bytes);
            return new Result(bookImage, Code.OK, "图片添加成功");
        } catch (IOException e) {
            throw new SystemException(Code.SYSTEM_ERR, "图片上传失败，请重试", e);
        }
    }

    @PutMapping("/updateBook")
    public Result updateBook(@RequestBody Book book) {
        bookManage.updateBook(book);
        return new Result(Code.OK, "图书数据更新完成");
    }
}
