package com.xsc.service;

import com.xsc.domain.Book;
import com.xsc.domain.BookImage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BookManage {
    List<Book> booksByUserId(Integer userId);

    List<BookImage> imageByBookId(Integer bookId);

    Integer replaceBookImage(Integer bookImageId, byte[] bookImageBytes);

    Integer deleteBookImage(Integer bookImageId);

    BookImage addBookImage(Integer bookId, byte[] imageBytes);

    void updateBook(Book book);

    void replaceBookCover(Integer bookId, byte[] bookCoverData);

    void takeDownBook(Integer bookId);
}
