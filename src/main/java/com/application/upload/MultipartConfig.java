package com.application.upload;

import org.apache.commons.fileupload.ProgressListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@EnableAutoConfiguration(exclude = MultipartAutoConfiguration.class)
public class MultipartConfig {
    /**
     *
     */
    @Bean
    public CustomMultipartResolver3 multipartResolver() {
        CustomMultipartResolver3 resolver = new CustomMultipartResolver3();
        resolver.setProgressListener(new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, int items) {
                double progress = (double) bytesRead / contentLength * 100;
                System.out.printf("Progress: %.2f%%%n", progress);
            }
        });
        return resolver;
    }
//    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver() {
//        try{
//            CommonsMultipartResolver multipartResolver =new CustomMultipartResolver();
//            multipartResolver.setMaxUploadSize(4*1024*1024);
//            multipartResolver.setMaxInMemorySize(1024);
//            multipartResolver.setUploadTempDir(new FileSystemResource("d:\\upload"));
//            return multipartResolver;
//        }catch(Exception e){
//
//        }
//        return null;
//    }
//    @Bean
//    public UploadProgressListener uploadProgressListener() {
//        return new UploadProgressListener();
//    }
//    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver() {
//        try{
//            StandardServletMultipartResolver multipartResolver =new CustomMultipartResolver2();
//            System.out.println("multipartResolver bean initialized" );
//            return multipartResolver;
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
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
    /**
     * 配置Multipart参数
     */
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//
//        // 设置单个文件最大大小为10MB
//        factory.setMaxFileSize(DataSize.ofMegabytes(4));
//        // 设置单次请求最大大小为20MB
//        factory.setMaxRequestSize(DataSize.ofMegabytes(20));
//        factory.setFileSizeThreshold(1024);
//        // 设置临时文件存储位置
//        String tempDir = System.getProperty("java.io.tmpdir");
//        System.out.println("MultipartConfigElement initialized with tempDir: " + tempDir);
//        factory.setLocation("d:\\upload");
//
//        return factory.createMultipartConfig();
//    }

}
