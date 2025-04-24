package com.application.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.application.common.Result;
import com.application.config.MultipartEnabled;
import com.application.domain.dao.req.TestInterceptorReq;
import com.application.upload.UploadProgressListener;
import com.application.utils.BeanUtils;
import com.application.utils.TimeUtils;
import com.application.utils.excel.DynamicExcelData;
import com.application.utils.excel.ExcelDynamicExport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
@Slf4j
public class FileController {
    static void f(Object obj) {
        System.out.println(obj);
    }
    /**
     * query
     *
     * @param id
     * @return
     */
    //@ApiOperation("query")
    //@UserLoginToken
    @GetMapping(value = "/query")
    public Result<Object> queryCountry(@RequestParam String id) {
        return Result.success(id);
    }

    @PostMapping("/testInterceptor")
    public Result<Object> testInterceptor(@RequestBody TestInterceptorReq request){
        return Result.success(request.getLineName());
    }
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        //模拟数据
        //一般动态数据使用的是List，然后内部使用Map进行数据的接受
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<>();
            for (int j = 0; j < 5; j++) {
                map.put("title"+j,i+","+j);
            }
            //这个用于测试值如果为null时，能否进行默认值填充
            map.put("title5",null);
            list.add(map);
        }


        //使用LinkedHashMap进行表头字段映射
        LinkedHashMap<String, DynamicExcelData> nameMap = new LinkedHashMap<>();
        nameMap.put("title1",new DynamicExcelData("年龄","0"));
        nameMap.put("title0",new DynamicExcelData("姓名","0"));
        nameMap.put("title2",new DynamicExcelData("职业","0"));
        nameMap.put("title3",new DynamicExcelData("爱好","0"));
        nameMap.put("title4",new DynamicExcelData("小名","0"));
        nameMap.put("title5",new DynamicExcelData("空白字段","0"));

        //调用方法,方法已在步骤3进行介绍
        ExcelDynamicExport.dynamicExport(response,nameMap, list,"模板");

    }
    /*
    * 这个方法涉及到request.getInputStream()结果中header信息的清除，不要用这个方法
    * */
    @PostMapping("/upload")
    public String upload(HttpServletRequest request, HttpServletResponse response){

        //CommonsMultipartResolver multipartResolver = (CommonsMultipartResolver) BeanUtils.getBean("multipartResolver");
        //System.out.println("isMultipart:"+multipartResolver.isMultipart(request));
        //StandardServletMultipartResolver multipartResolver = (StandardServletMultipartResolver) BeanUtils.getBean("multipartResolver");
        //System.out.println("isMultipart:"+multipartResolver.isMultipart(request));
        System.out.println("1:"+TimeUtils.getNowTime());
        String fileName = request.getHeader("filename");
        File targetFile = new File("d:\\upload2", fileName);
        if(targetFile.exists()){
            targetFile.delete();
        }
        //System.out.println("1:"+TimeUtils.getNowTime());
        // 使用FileChannel直接写入磁盘
        String requestContentType = request.getContentType();
        System.out.println("requestContentType:"+requestContentType);
        //String responseContentType = response.getContentType();
        //System.out.println("responseContentType:"+responseContentType);

        //String boundary = "--" + requestContentType.split("boundary=")[1].replace("\"", "");  // 如: "--abc123"
        //System.out.println("boundary:"+boundary);


        try {
            InputStream is = request.getInputStream();
            long contentLengthLong = request.getContentLengthLong();
            System.out.println("contentLengthLong:"+contentLengthLong);
            System.out.println("2:"+TimeUtils.getNowTime());
            FileChannel channel = FileChannel.open(
                    targetFile.toPath(),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);
            // 通过通道传输数据
            ByteBuffer buffer = ByteBuffer.allocateDirect(8192); // 直接缓冲区提升性能
            ReadableByteChannel sourceChannel = Channels.newChannel(is);

            while (sourceChannel.read(buffer) != -1) {
                buffer.flip();
                channel.write(buffer);
                buffer.clear();
            }
            buffer = null;
            is.close();
            channel.close();
            sourceChannel.close();
            System.out.println("3:"+TimeUtils.getNowTime());
            return "Upload success: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }

    }
    /**
     *
     */
    //@MultipartEnabled
    @PostMapping(path = "/upload2")
    public ResponseEntity<String> upload2(@RequestParam(value = "file") MultipartFile file,HttpServletRequest request){
        //StandardServletMultipartResolver multipartResolver = (StandardServletMultipartResolver) BeanUtils.getBean("multipartResolver");
        //System.out.println("2 isMultipart:"+multipartResolver.isMultipart(request));
//        if(!multipartResolver.isMultipart(request)){
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Multipart upload not allowed on this path");
//        }
//        try {
//            // 将 uploadId 绑定到当前线程
//            UploadProgressListener.setCurrentUploadId("1");
//
//            // 模拟文件保存（实际替换为你的业务逻辑）
//            Files.copy(file.getInputStream(), Paths.get("d:\\tmp\\" + file.getOriginalFilename()));
//
//            return ResponseEntity.ok("Upload success");
//        }catch (Exception e){
//
//        }finally {
//            // 清理线程变量
//            UploadProgressListener.clear();
//        }
            System.out.println("start:"+ TimeUtils.getNowTime());
        ReadableByteChannel inChannel = null;
        FileChannel outChannel = null;
        FileOutputStream fos = null;
        try{
            inChannel = Channels.newChannel(file.getInputStream());
            System.out.println("inChannel:"+ TimeUtils.getNowTime());
            fos = new FileOutputStream("d:\\tmp\\"+ File.separator+file.getOriginalFilename());
            System.out.println("new FileOutputStream:"+ TimeUtils.getNowTime());
            outChannel = fos.getChannel();
            System.out.println("fos.getChannel:"+ TimeUtils.getNowTime());
            outChannel.transferFrom(inChannel,0,file.getSize());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("finally:"+ TimeUtils.getNowTime());
            //关闭资源
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return ResponseEntity.ok("File uploaded successfully");
    }
    /**
     *
     */
    //@MultipartEnabled
    @PostMapping(path = "/upload3")
    public ResponseEntity<String> upload3(@RequestParam(value = "file") MultipartFile file,HttpServletRequest request){
        //StandardServletMultipartResolver multipartResolver = (StandardServletMultipartResolver) BeanUtils.getBean("multipartResolver");
        //System.out.println("3 isMultipart:"+multipartResolver.isMultipart(request));
        //CommonsMultipartResolver multipartResolver = (CommonsMultipartResolver) BeanUtils.getBean("multipartResolver");
        //System.out.println("size:"+multipartResolver.getFileItemFactory().getSizeThreshold());
        //multipartResolver.setMaxUploadSize(10*1024*1024);
        System.out.println("start:"+ TimeUtils.getNowTime());
        ReadableByteChannel inChannel = null;
        FileChannel outChannel = null;
        FileOutputStream fos = null;
        try{
            inChannel = Channels.newChannel(file.getInputStream());
            System.out.println("inChannel:"+ TimeUtils.getNowTime());
            fos = new FileOutputStream("d:\\tmp\\"+ File.separator+file.getOriginalFilename());
            System.out.println("new FileOutputStream:"+ TimeUtils.getNowTime());
            outChannel = fos.getChannel();
            System.out.println("fos.getChannel:"+ TimeUtils.getNowTime());
            outChannel.transferFrom(inChannel,0,file.getSize());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("finally:"+ TimeUtils.getNowTime());
            //关闭资源
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return ResponseEntity.ok("File uploaded successfully");
    }
    /**
     *
     */
    //@MultipartEnabled
    @PostMapping(path = "/upload4")
    public ResponseEntity<String> upload4(HttpServletRequest request){


        return ResponseEntity.ok("File uploaded successfully");
    }
    /*
    * 还是会先放到临时目录
    * */
    @PostMapping(path = "/upload5")
    public ResponseEntity<String> upload5(HttpServletRequest request,HttpServletResponse response){

        try{
            String uploadDir = "d:\\upload2\\";
            if (ServletFileUpload.isMultipartContent(request)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                try {
                    List<FileItem> items = upload.parseRequest(request);
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
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok("File uploaded successfully");
    }
    @PostMapping("/upload6")
    public ResponseEntity<String> upload6(@RequestParam("file") MultipartFile file) {
        // 文件处理逻辑
        return ResponseEntity.ok("File uploaded successfully");
    }
    @PostMapping("/upload7")
    public void upload7(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        String tempDir = System.getProperty("java.io.tmpdir");
        System.out.println("tempDir: " + tempDir);
        try {
            String uploadDir = "d:\\upload2";
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    // 确保上传目录存在
                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    // 保存文件
                    File targetFile = new File(uploadDir, item.getName());
                    if(targetFile.exists()){
                        targetFile.delete();
                    }
                    InputStream is = item.getInputStream();

                    FileChannel channel = FileChannel.open(
                            targetFile.toPath(),
                            StandardOpenOption.CREATE_NEW,
                            StandardOpenOption.WRITE);
                    // 通过通道传输数据
                    ByteBuffer buffer = ByteBuffer.allocateDirect(8192); // 直接缓冲区提升性能
                    ReadableByteChannel sourceChannel = Channels.newChannel(is);

                    while (sourceChannel.read(buffer) != -1) {
                        buffer.flip();
                        channel.write(buffer);
                        buffer.clear();
                    }
                    buffer = null;
                    is.close();
                    channel.close();
                    sourceChannel.close();
                    System.out.println("1:" + TimeUtils.getNowTime());
                }

            }
            response.getWriter().write("success");
        } catch (Exception e) {
            response.getWriter().write("fail:" + e.getMessage());
        }
    }
    private static byte[] subBytes(byte[] b, int from, int end) {
        byte[] result = new byte[end - from];
        System.arraycopy(b, from, result, 0, end - from);
        return result;
    }
    /**
     * 定位当前头信息的结束位置
     * @param bytes
     * @param start :开始位置
     * @param end :结束位置
     * @param endStr :比较字符串
     * @return
     * TODO
     */
    public int locateEnd(byte[] bytes,int start,int end,String endStr){
        byte[] endByte = endStr.getBytes();
        for(int i=start+1;i<end;i++){
            if(bytes[i]==endByte[0]){
                int k = 1;
                while(k<endByte.length){
                    if(bytes[i+k] != endByte[k]){
                        break;
                    }
                    k++;
                }
                System.out.println(i);
                if(i==3440488){
                    System.out.println("start");
                }
                //返回结束符的开始位置
                if(k == endByte.length){
                    return i;
                }
            }
        }

        return 0;
    }
    /*
     * 这个方法涉及到request.getInputStream()结果中header信息的清除，不要用这个方法
     * */
    @PostMapping("/upload8")
    public void upload8(HttpServletRequest request, HttpServletResponse response) throws IOException {
//      1.判断当前request消息实体的总长度
        int totalBytes = request.getContentLength();
        System.out.println("当前数据总长度:" + totalBytes);
//      2.在消息头类型中找出分解符,例如:boundary=----WebKitFormBoundaryeEYAk4vG4tRKAlB6
        String contentType = request.getContentType();
        int position = contentType.indexOf("boundary=");

        String startBoundary = "--" + contentType.substring(position + "boundary=".length());
        String endBoundary = startBoundary + "--";
        //将request的输入流读入到bytes中
        InputStream inputStream = request.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] bytes = new byte[totalBytes];
        dataInputStream.readFully(bytes);
        dataInputStream.close();
        //将字节读入到字符流中
        BufferedReader reader = new BufferedReader(new StringReader(new String(bytes)));

        //开始读取reader(分割form表单内的表单域类型:文本或者文件)

        //记录当前的读取行对应的bytes;
        int temPosition = 0;
        boolean flag = false;
        int end = 0;
        while (true) {
            //当读取一次文件信息后
            if (flag) {
                bytes = subBytes(bytes, end, totalBytes);
                temPosition = 0;
                reader = new BufferedReader(new StringReader(new String(bytes)));
            }
            //读取一行的信息:------WebKitFormBoundary5R7esAd459uwQsd5,即:lastBoundary
            String str = reader.readLine();
            System.out.println("this line is:" + str);
            //换行算两个字符
            temPosition += str.getBytes().length + 2;
            //endBoundary:结束
            if (str == null || str.equals(endBoundary)) {
                break;
            }
            //表示头信息的开始(一个标签,input,select等)
            if (str.startsWith(startBoundary)) {
                //判断当前头对应的表单域类型

                str = reader.readLine(); //读取当前头信息的下一行:Content-Disposition行
                temPosition += str.getBytes().length + 2;

                int position1 = str.indexOf("filename="); //判断是否是文件上传
                //such as:Content-Disposition: form-data; name="fileName"; filename="P50611-162907.jpg"
                if (position1 == -1) {//表示是普通文本域上传

                } else {//position1!=-1,表示是文件上传
                    //解析当前上传的文件对应的name(input标签的name),以及fieldname:文件名
                    int position2 = str.indexOf("name=");
                    //去掉name与filename之间的"和;以及空格
                    String name = str.substring(position2 + "name=".length() + 1, position1 - 3);
                    //去掉两个"
                    String filename = str.substring(position1 + "filename=".length() + +1, str.length() - 1);

                    //读取行,such as:Content-Type: image/jpeg,记录字节数,此处两次换行
                    temPosition += (reader.readLine().getBytes().length + 4);
                    end = this.locateEnd(bytes, temPosition, totalBytes, endBoundary);
                    String path = request.getSession().getServletContext().getRealPath("/");
                    DataOutputStream dOutputStream = new DataOutputStream(new FileOutputStream(new File(path + "/test.jpg")));
                    dOutputStream.write(bytes, temPosition, end - temPosition - 2);
                    dOutputStream.close();

                    flag = true;

                }
            }
        }
    }
    /*
     * 这个方法涉及到request.getInputStream()结果中header信息的清除，用的堆内存缓冲区，文本文件类型已成功
     * */
    @PostMapping("/upload9")
    public String upload9(HttpServletRequest request, HttpServletResponse response,@RequestParam (value="filename") String fileName){
        System.out.println("1:"+TimeUtils.getNowTime());
        //String fileName = request.getHeader("filename");
        File targetFile = new File("d:\\upload2\\"+fileName);
        if(targetFile.exists()){
            targetFile.delete();
        }
        String accept_encoding = request.getHeader("Accept-Encoding");
        f("accept_encoding:"+accept_encoding);

        String content_length = request.getHeader("Content-Length");
        f("content_length:"+content_length);
        //System.out.println("1:"+TimeUtils.getNowTime());
        // 使用FileChannel直接写入磁盘
        String requestContentType = request.getContentType();
        System.out.println("requestContentType:"+requestContentType);
        //String responseContentType = response.getContentType();
        //System.out.println("responseContentType:"+responseContentType);
        long contentLengthLongTotalLong = request.getContentLengthLong();
        System.out.println("contentLengthLongTotalLong:"+contentLengthLongTotalLong);
        String boundary = "--" + requestContentType.split("boundary=")[1].replace("\"", "");  // 如: "--abc123"
        System.out.println("boundary:"+boundary);
        String line1 = boundary;
        f("line1_len:"+line1.length());
        String line2 = "Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\"";
        f("line2_len:"+line2.length());
        String line3 = "";
        int sub = 104;
        if(fileName.endsWith(".txt")){
            line3 = "Content-Type: text/plain";
            sub = 104;
        }else if(fileName.endsWith(".exe")){
            line3 = "Content-Type: application/x-msdos-program";
            sub = 121;
        }else{
            line3 = "Content-Type: application/octet-stream";
            sub = 118;
        }
        f("line3_len:"+line3.length());
        String line4 = "";
        f("line4_len:"+line4.length());
        String line5 = boundary+"--";
        f("line5_len:"+line5.length());

        String headStart = line1+line2+line3+line4;
        String headEnd = line5;
        System.out.print("headStart:"+headStart);
        System.out.print("headEnd:"+headEnd);
        long headStartByteLength = headStart.getBytes().length;
        long headEndByteLength = headEnd.getBytes().length;
        System.out.println("headStartByteLength:"+headStartByteLength);
        System.out.println("headEndByteLength:"+headEndByteLength);
        int bufferSize = 8192;

        try {
            InputStream inputStream = request.getInputStream();
            FileOutputStream ouputStream = new FileOutputStream("d:\\upload2\\"+fileName);
            long contentLengthLongTotal = contentLengthLongTotalLong - 12;

            long contentLengthLong = contentLengthLongTotal - headStartByteLength - headEndByteLength;
            if(contentLengthLong == 0){
                inputStream.close();
                ouputStream.flush();
                ouputStream.close();
                return "Upload success: 1";
            }
            System.out.println("contentLengthLongTotal:"+contentLengthLongTotal);
            System.out.println("contentLengthLong:"+contentLengthLong);

            System.out.println("2:"+TimeUtils.getNowTime());
            byte b[] = new byte[bufferSize];
            //int n;
            //long times = contentLengthLong / (long)bufferSize;
            //long mod = contentLengthLong % (long)bufferSize;
            int fileNameLength = fileName.length();
            int headLength = sub + fileNameLength;
            int headTimes = (headLength / bufferSize) + 1;
            f("headTimes:"+headTimes);
            int headMod = headLength % bufferSize;
            f("headMod:"+headMod);
            for(int i = 1;i <= headTimes;i++){
                int readCount = 0; // 已经成功读取的字节的个数
                while (readCount < bufferSize) {
                    int readLen = inputStream.read(b, readCount, bufferSize - readCount);
                    if(readLen == -1){
                        break;
                    }
                    readCount += readLen;
                }
            }
            long contentLengthTimes = ((contentLengthLong + headMod) / (long)bufferSize) + 1;
            for(long i = 1;i <= contentLengthTimes;i++){
                //f("contentLengthLong:"+contentLengthLong);
                if(i == 1){
                    int remainLen = bufferSize - headMod;
                    //f("remainLen:"+remainLen);
                    if(contentLengthLong < remainLen){
                        ouputStream.write(b, headMod, (int)contentLengthLong);
                        contentLengthLong = contentLengthLong - contentLengthLong;
                        //break;
                    }else{
                        ouputStream.write(b, headMod, remainLen);
                        contentLengthLong = contentLengthLong - remainLen;
                        //f("contentLengthLong:"+contentLengthLong);
                    }
                }else{
                    if(contentLengthLong < bufferSize){
                        ouputStream.write(b, 0, (int)contentLengthLong);
                        contentLengthLong = contentLengthLong - contentLengthLong;
                        //break;
                    }else{
                        ouputStream.write(b, 0, bufferSize);
                        contentLengthLong = contentLengthLong - bufferSize;
                    }
                }
                int readCount = 0; // 已经成功读取的字节的个数
                while (readCount < bufferSize) {
                    int readLen = inputStream.read(b, readCount, bufferSize - readCount);
                    if(readLen == -1){
                        break;
                    }
                    readCount += readLen;
                }
            }
            inputStream.close();
            ouputStream.flush();
            ouputStream.close();
            b = null;
            System.out.println("3:"+TimeUtils.getNowTime());
            return "Upload success: ";
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }

    /*
     * 这个方法涉及到request.getInputStream()结果中header信息的清除，用channel，没成功
     * */
    @PostMapping("/upload10")
    public String upload10(HttpServletRequest request, HttpServletResponse response,@RequestParam (value="filename") String fileName){
        System.out.println("1:"+TimeUtils.getNowTime());
        File targetFile = new File("d:\\upload2\\"+fileName);
        if(targetFile.exists()){
            targetFile.delete();
        }
        String accept_encoding = request.getHeader("Accept-Encoding");
        f("accept_encoding:"+accept_encoding);
        String content_length = request.getHeader("Content-Length");
        f("content_length:"+content_length);
        String requestContentType = request.getContentType();
        System.out.println("requestContentType:"+requestContentType);
        long contentLengthLongTotalLong = request.getContentLengthLong();
        System.out.println("contentLengthLongTotalLong:"+contentLengthLongTotalLong);
        String boundary = "--" + requestContentType.split("boundary=")[1].replace("\"", "");  // 如: "--abc123"
        System.out.println("boundary:"+boundary);
        String line1 = boundary;
        f("line1_len:"+line1.length());
        String line2 = "Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\"";
        f("line2_len:"+line2.length());
        String line3 = "";
        //if(fileName.endsWith(".txt")){
        line3 = "Content-Type: text/plain";
        //}
        f("line3_len:"+line3.length());
        String line4 = "";
        f("line4_len:"+line4.length());
        String line5 = boundary+"--";
        f("line5_len:"+line5.length());

        String headStart = line1+line2+line3+line4;
        String headEnd = line5;
        System.out.print("headStart:"+headStart);
        System.out.print("headEnd:"+headEnd);
        long headStartByteLength = headStart.getBytes().length;
        long headEndByteLength = headEnd.getBytes().length;
        System.out.println("headStartByteLength:"+headStartByteLength);
        System.out.println("headEndByteLength:"+headEndByteLength);
        try {
            InputStream inputStream = request.getInputStream();
            FileOutputStream ouputStream = new FileOutputStream("d:\\upload2\\"+fileName);
            ReadableByteChannel inChannel = null;
            FileChannel outChannel = null;

            long contentLengthLongTotal = contentLengthLongTotalLong - 12;

            long contentLengthLong = contentLengthLongTotal - headStartByteLength - headEndByteLength;
            if(contentLengthLong == 0){
                inputStream.close();
                ouputStream.flush();
                ouputStream.close();
                return "Upload success: 1";
            }
            System.out.println("contentLengthLongTotal:"+contentLengthLongTotal);
            System.out.println("contentLengthLong:"+contentLengthLong);

            System.out.println("2:"+TimeUtils.getNowTime());

            int fileNameLength = fileName.length();
            int headLength = 104 + fileNameLength;

            inChannel = Channels.newChannel(inputStream);
            //inChannel.read();
            //通道间传输

            outChannel = ouputStream.getChannel();
            //上传
            long transferLen = outChannel.transferFrom(inChannel,1,contentLengthLongTotal);
            f("transferLen:"+transferLen);
            transferLen = outChannel.transferFrom(inChannel,1,contentLengthLongTotal);
            f("transferLen:"+transferLen);
            try{
                outChannel.close();
                inChannel.close();
                ouputStream.close();
                inputStream.close();

            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("3:"+TimeUtils.getNowTime());
            return "Upload success: ";
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }
    /*
     * 这个方法涉及到request.getInputStream()结果中header信息的清除，用到直接内存缓冲区，成功了
     * */
    @PostMapping("/upload11")
    public String upload11(HttpServletRequest request, HttpServletResponse response,@RequestParam (value="filename") String fileName){
        System.out.println("1:"+TimeUtils.getNowTime());
        //String fileName = request.getHeader("filename");
        File targetFile = new File("d:\\upload2\\"+fileName);
        if(targetFile.exists()){
            targetFile.delete();
        }
        String accept_encoding = request.getHeader("Accept-Encoding");
        f("accept_encoding:"+accept_encoding);

        String content_length = request.getHeader("Content-Length");
        f("content_length:"+content_length);
        //System.out.println("1:"+TimeUtils.getNowTime());
        // 使用FileChannel直接写入磁盘
        String requestContentType = request.getContentType();
        System.out.println("requestContentType:"+requestContentType);
        //String responseContentType = response.getContentType();
        //System.out.println("responseContentType:"+responseContentType);
        long contentLengthLongTotalLong = request.getContentLengthLong();
        System.out.println("contentLengthLongTotalLong:"+contentLengthLongTotalLong);
        String boundary = "--" + requestContentType.split("boundary=")[1].replace("\"", "");  // 如: "--abc123"
        System.out.println("boundary:"+boundary);
        String line1 = boundary;
        f("line1_len:"+line1.length());
        String line2 = "Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\"";
        f("line2_len:"+line2.length());
        String line3 = "";
        int sub = 104;
        if(fileName.endsWith(".txt")){
            line3 = "Content-Type: text/plain";
            sub = 104;
        }else if(fileName.endsWith(".exe")){
            line3 = "Content-Type: application/x-msdos-program";
            sub = 121;
        }else{
            line3 = "Content-Type: application/octet-stream";
            sub = 118;
        }
        f("line3_len:"+line3.length());
        String line4 = "";
        f("line4_len:"+line4.length());
        String line5 = boundary+"--";
        f("line5_len:"+line5.length());

        String headStart = line1+line2+line3+line4;
        String headEnd = line5;
        System.out.print("headStart:"+headStart);
        System.out.print("headEnd:"+headEnd);
        long headStartByteLength = headStart.getBytes().length;
        long headEndByteLength = headEnd.getBytes().length;
        System.out.println("headStartByteLength:"+headStartByteLength);
        System.out.println("headEndByteLength:"+headEndByteLength);
        int bufferSize = 8192;
        //System.exit(0);
        try {
            InputStream inputStream = request.getInputStream();
            FileOutputStream ouputStream = new FileOutputStream("d:\\upload2\\"+fileName);
            ReadableByteChannel inChannel = null;
            FileChannel outChannel = null;

            long contentLengthLongTotal = contentLengthLongTotalLong - 12;

            long contentLengthLong = contentLengthLongTotal - headStartByteLength - headEndByteLength;
            if(contentLengthLong == 0){
                inputStream.close();
                ouputStream.flush();
                ouputStream.close();
                return "Upload success: 1";
            }
            System.out.println("contentLengthLongTotal:"+contentLengthLongTotal);
            System.out.println("contentLengthLong:"+contentLengthLong);

            System.out.println("2:"+TimeUtils.getNowTime());
            //byte b[] = new byte[bufferSize];
            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
            inChannel = Channels.newChannel(inputStream);
            outChannel = ouputStream.getChannel();
//            while (inChannel.read(buffer) != -1) {
//                buffer.flip();
//                outChannel.write(buffer);
//                buffer.clear();
//                break;
//            }
//            System.exit(0);
            //int n;
            //long times = contentLengthLong / (long)bufferSize;
            //long mod = contentLengthLong % (long)bufferSize;
            int fileNameLength = fileName.length();
            int headLength = sub + fileNameLength;
            int headTimes = (headLength / bufferSize) + 1;
            f("headTimes:"+headTimes);
            int headMod = headLength % bufferSize;
            f("headMod:"+headMod);
            for(int i = 1;i <= headTimes;i++){

                int readCount = 0; // 已经成功读取的字节的个数
                while (readCount < bufferSize) {
                    int readLen = inChannel.read(buffer);
                    if(readLen == -1){
                        break;
                    }
                    readCount += readLen;
                }

            }
            //buffer.flip();
//            buffer.position(7).limit(30);
//            outChannel.write(buffer);
//            System.exit(0);
            long contentLengthTimes = ((contentLengthLong + headMod) / (long)bufferSize) + 1;
            for(long i = 1;i <= contentLengthTimes;i++){
                //f("contentLengthLong:"+contentLengthLong);
                if(i == 1){
                    int remainLen = bufferSize - headMod;
                    //f("remainLen:"+remainLen);
                    if(contentLengthLong < remainLen){
                        buffer.position(headMod).limit(headMod + (int)contentLengthLong);
                        outChannel.write(buffer);
                        buffer.clear();
                        contentLengthLong = contentLengthLong - contentLengthLong;
                        //break;
                    }else{
                        buffer.position(headMod).limit(headMod + remainLen);
                        outChannel.write(buffer);
                        buffer.clear();
                        contentLengthLong = contentLengthLong - remainLen;
                        //f("contentLengthLong:"+contentLengthLong);
                    }
                }else{
                    if(contentLengthLong < bufferSize){
                        buffer.position(0).limit((int)contentLengthLong);
                        outChannel.write(buffer);
                        buffer.clear();
                        contentLengthLong = contentLengthLong - contentLengthLong;
                        //break;
                    }else{
                        buffer.position(0).limit(bufferSize);
                        outChannel.write(buffer);
                        buffer.clear();
                        contentLengthLong = contentLengthLong - bufferSize;
                    }
                }
                int readCount = 0; // 已经成功读取的字节的个数
                while (readCount < bufferSize) {
                    int readLen = inChannel.read(buffer);
                    if(readLen == -1){
                        break;
                    }
                    readCount += readLen;
                }
            }
            outChannel.close();
            inChannel.close();
            inputStream.close();
            ouputStream.flush();
            ouputStream.close();

            buffer = null;
            System.out.println("3:"+TimeUtils.getNowTime());
            return "Upload success: ";
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }

    public static void main(String[] args) throws IOException {
        File destiFile=new File("d:\\a.txt");
//        BufferedWriter destiBufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destiFile), "UTF-8"));
//        for(int i=0;i<1000000;i++){
//            destiBufferedWriter.write(i+"\n");
//            destiBufferedWriter.flush();
//        }
//        destiBufferedWriter.close();
        FileInputStream fis = new FileInputStream(destiFile);
        f("len:"+destiFile.length());
        long a = 6888999 / 1024;
        f("a:"+a);
    }

}