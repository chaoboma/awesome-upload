package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(scanBasePackages = {"com.application"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public ServletRegistrationBean<FileUploadServlet> servletRegistrationBean() {
        ServletRegistrationBean<FileUploadServlet> bean = new ServletRegistrationBean<>(new FileUploadServlet(), "/upload");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
