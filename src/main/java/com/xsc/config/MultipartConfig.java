package com.xsc.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartConfig {

    /**
     * 文件上传配置
     *
     * @return 文件解析器
     */
    @Bean  //multipart resolver 文件上传解析
    public CommonsMultipartResolver multipartConfigElement() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(1024 * 1024 * 5);// 文件最大上传大小，不限制了
        multipartResolver.setDefaultEncoding("utf-8");
        return multipartResolver;
    }
}

