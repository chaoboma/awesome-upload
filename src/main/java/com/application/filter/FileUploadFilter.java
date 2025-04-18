package com.application.filter;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
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
