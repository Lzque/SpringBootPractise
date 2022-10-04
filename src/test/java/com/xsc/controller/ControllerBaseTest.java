package com.xsc.controller;

import com.xsc.SpringBootUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@WebAppConfiguration
public abstract class ControllerBaseTest extends SpringBootUnitTest {
    protected final static String codePath = "$.code";
    protected final static String dataPath = "$.data";
    protected final static String messagePath = "$.message";
    /*
     * 1、MockMvcRequestBuilders.post("XXX")构造一个post请求。
     * 2、mockMvc.perform执行一个请求。
     * 3、ResultActions.param添加请求传值
     * 4、ResultActions.accept(MediaType.TEXT_HTML_VALUE)设置返回类型
     * 5、ResultActions.andExpect添加执行完成后的断言。
     * 6、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情
     *    比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
     * 7、ResultActions.andReturn表示执行完成后返回相应的结果。
     */
    @Autowired
    protected WebApplicationContext wac;
    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    public ResultActions buildRequest(Supplier<MockHttpServletRequestBuilder> method) throws Exception {
        String header = getBaseHeader();
        header = header == null ? "" : header;
        return this.mockMvc.perform(method.get().characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, header));
        // .andDo(MockMvcResultHandlers.print())
    }

    public abstract String getBaseHeader();
}
