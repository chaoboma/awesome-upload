package com.application.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
@EnableAutoConfiguration(exclude = MultipartAutoConfiguration.class)
public class MultipartConfig {
    /**
     *
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        try{
            CommonsMultipartResolver multipartResolver =new CustomMultipartResolver();
            multipartResolver.setMaxUploadSize(4*1024*1024);
            multipartResolver.setMaxInMemorySize(1024);
            multipartResolver.setUploadTempDir(new FileSystemResource("d:\\upload"));
            return multipartResolver;
        }catch(Exception e){

        }
        return null;
    }
//    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver(){
//        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//        resolver.setDefaultEncoding("UTF-8");
//        //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
//        resolver.setResolveLazily(true);
//        resolver.setMaxInMemorySize(40960);
//        //上传文件大小 50M 50*1024*1024
//        resolver.setMaxUploadSize(50*1024*1024);
//        return resolver;
//    }
}
