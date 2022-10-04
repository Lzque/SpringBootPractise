package com.xsc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xsc.domain.Book;
import com.xsc.domain.BookImage;
import com.xsc.mapper.BookImageMapper;
import com.xsc.mapper.BookMapper;
import com.xsc.service.BookManage;
import com.xsc.utils.ImageHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class BookManageImpl implements BookManage {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookImageMapper bookImageMapper;

    @Override
    public List<Book> booksByUserId(Integer userId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getUserId, userId).orderByDesc(Book::getId);
        List<Book> books = bookMapper.selectList(wrapper);
        for (Book book : books) {
            book.setImagePath("data:image/png;base64," + Base64.getEncoder().encodeToString(book.getCoverData()));
            book.setCoverData(null);
        }
        return books;
    }

    @Override
    public List<BookImage> imageByBookId(Integer bookId) {
        LambdaQueryWrapper<BookImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookImage::getBookId, bookId);
        List<BookImage> bookImages = bookImageMapper.selectList(wrapper);
        for (BookImage bookImage : bookImages) {
            bookImage.setBase64String("data:image/png;base64," + Base64.getEncoder().encodeToString(bookImage.getImage()));
            bookImage.setImage(null);
        }
        return bookImages;
    }

    @Override
    public Integer replaceBookImage(Integer bookImageId, byte[] bookImageBytes) {
        LambdaUpdateWrapper<BookImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BookImage::getId, bookImageId).set(BookImage::getImage, bookImageBytes);
        return bookImageMapper.update(null, wrapper);
    }

    @Override
    public Integer deleteBookImage(Integer bookImageId) {
        return bookImageMapper.deleteById(bookImageId);
    }

    @Override
    public BookImage addBookImage(Integer bookId, byte[] imageBytes) {
        BookImage bookImage = new BookImage();
        bookImage.setBookId(bookId);
        bookImage.setImage(imageBytes);
        bookImageMapper.insert(bookImage);
        bookImage.setBase64String("data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes));
        return bookImage;
    }

    @Override
    public void updateBook(Book book) {
        book.setSales(null);
        book.setUserId(null);
        book.setOverallRating(null);
        book.setCoverData(null);
        bookMapper.updateById(book);
    }

    @Override
    public void replaceBookCover(Integer bookId, byte[] bookCoverData) {
        LambdaUpdateWrapper<Book> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Book::getId, bookId).set(Book::getCoverData, bookCoverData);
        bookMapper.update(null, wrapper);
    }

    @Override
    public void takeDownBook(Integer bookId) {
        bookMapper.deleteById(bookId);
    }
}
