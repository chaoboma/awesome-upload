package com.application.config;


import com.application.filter.FileUploadFilter;
import com.application.filter.SignPerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
public class FilterConfig {
    @Value("${filter.timeout}")
    private Integer timeout;
 //   @Bean
 //   public FilterRegistrationBean decryptingFilterRegistration() {
 //       FilterRegistrationBean registrationBean =
//            new FilterRegistrationBean();
        //注册过滤器
        //SignPerRequestFilter filter = new SignPerRequestFilter();
        //filter.timeout = timeout;
        //registrationBean.setFilter(filter);
        //registrationBean.addUrlPatterns("/*"); // 设置过滤器应用的URL模式
        //registrationBean.setOrder(1); // 设置过滤器的顺序
//        FileUploadFilter fileUploadFilter = new FileUploadFilter();
//        registrationBean.setFilter(fileUploadFilter);
//        registrationBean.addUrlPatterns("/file/upload4");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
