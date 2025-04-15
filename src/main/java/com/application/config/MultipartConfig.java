package com.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        //return new CustomMultipartResolver();
        CustomMultipartResolver resolver = new CustomMultipartResolver();
        resolver.setMaxUploadSize(52428800); // 设置最大上传大小为50MB
        //resolver.isMultipart();
        return resolver;

    }
}