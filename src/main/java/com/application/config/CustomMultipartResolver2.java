package com.application.config;

import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;

public class CustomMultipartResolver2 extends StandardServletMultipartResolver {

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 检查路径是否以允许的路径开头，例如/api/upload
        if (path.startsWith("/file/upload3")) {
            return true;
        }
        return false;
    }
}
