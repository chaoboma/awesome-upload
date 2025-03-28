package com.application;



import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/upload")
public class FileUploadServlet extends HttpServlet {
    @Value("${file.path}")
    private String localFilePath;
    private static final long serialVersionUID = 1L;
    //private static final String UPLOAD_DIRECTORY = localFilePath;
    public static String getNowTime(){
        Date date = new Date();
        String stime = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        stime = df.format(date);
        return stime;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent( request)) {
            response.getWriter().println("Form must have enctype=multipart/form-data.");
            return;
        }
        System.out.println("1:"+ getNowTime());
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        System.out.println("2:"+ getNowTime());
        try {
            List<FileItem> formItems = upload.parseRequest(request);
            System.out.println("3:"+ getNowTime());
            for (FileItem item : formItems) {
                if (!item.isFormField()) {
                    String fileName = new File(item.getName()).getName();
                    File storeFile = new File("d:\\" + File.separator + fileName);
                    System.out.println("4:"+ getNowTime());
                    try (InputStream inputStream = item.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(storeFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("5:"+ getNowTime());
                    response.getWriter().println("File uploaded successfully: " + fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
