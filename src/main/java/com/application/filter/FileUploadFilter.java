package com.application.filter;

import com.application.utils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@WebFilter(urlPatterns = "/file/upload4")
public class FileUploadFilter implements Filter {

    private final String uploadDir = "d:\\upload\\";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //((HttpServletResponse) response).sendError(400, "Multipart upload not allowed on this path");
        CommonsMultipartResolver multipartResolver = (CommonsMultipartResolver) BeanUtils.getBean("multipartResolver");
        boolean a = multipartResolver.isMultipart(httpRequest);
        System.out.println("2 isMultipart:"+multipartResolver.isMultipart(httpRequest));
        if(!multipartResolver.isMultipart(httpRequest)){
            ((HttpServletResponse) response).sendError(400, "Multipart upload not allowed on this path");
            return;
        }
        System.out.println("ServletFileUpload.isMultipartContent(httpRequest):"+ServletFileUpload.isMultipartContent(httpRequest));
        if (ServletFileUpload.isMultipartContent(httpRequest)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> items = upload.parseRequest(httpRequest);
                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        // 确保上传目录存在
                        File directory = new File(uploadDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        // 保存文件
                        File uploadFile = new File(uploadDir, item.getName());
                        item.write(uploadFile);
                    }
                }
                response.getWriter().write("success");
            } catch (Exception e) {
                response.getWriter().write("fail:" + e.getMessage());
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
