package com.xsc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsc.domain.Book;
import com.xsc.mapper.BookMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class BookManageControllerTest extends ControllerBaseTest {
    @Autowired
    private BookMapper bookMapper;

    @Test
    void testBooksLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders.get("/bookManage");
        super.buildRequest(() -> requestBuilder1).
                andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(4001)));
        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders.get("/bookManage").sessionAttr("id", sellerUserId);
        super.buildRequest(() -> requestBuilder2).
                andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testEditImageLoad() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bookManage/editImageLoad/" + bookId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testReplaceBookImage() throws Exception {
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/bookManage/replaceBookImage/" + bookImageId + "/" + 0 + "/" + bookId)
//                .file(multipartFile)
//        super.buildRequest(() -> requestBuilder)
//                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        /*mockjax只支持ajax请求，不支持form表单，所以失败，所以使用postman测试 */
    }

    @Test
    void testDeleteBookImage() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/bookManage/deleteBookImage/" + bookImageId);
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
    }

    @Test
    void testAddBookImage() throws Exception {
        /*postman测试，因为有文件上传*/
    }

    @Test
    void testUpdateBook() throws Exception {
        Book book = new Book();
        book.setId(bookId);
        book.setBookName("测试书名更新");
        book.setAuthor("测试作者更新");
        book.setPublishingHouse("测试出版社更新");
        book.setSort("测试类别更新");
        book.setPrice(9.9);
        book.setUserId(sellerUserId);
        book.setInStock(9);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/bookManage/updateBook")
                .content(new ObjectMapper().writeValueAsString(book));
        super.buildRequest(() -> requestBuilder)
                .andExpect(MockMvcResultMatchers.jsonPath(codePath, Matchers.is(2001)));
        Book selectByIdBook = bookMapper.selectById(bookId);
        Assertions.assertEquals(selectByIdBook.getBookName(), book.getBookName());
        Assertions.assertEquals(selectByIdBook.getAuthor(), book.getAuthor());
        Assertions.assertEquals(selectByIdBook.getPublishingHouse(), book.getPublishingHouse());
        Assertions.assertEquals(selectByIdBook.getSort(), book.getSort());
        Assertions.assertEquals(selectByIdBook.getPrice(), book.getPrice());
        Assertions.assertEquals(selectByIdBook.getInStock(), book.getInStock());
    }

    @Override
    public String getBaseHeader() {
        return null;
    }
}
