package com.application.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.application.common.Result;
import com.application.config.MultipartEnabled;
import com.application.domain.dao.req.TestInterceptorReq;
import com.application.utils.BeanUtils;
import com.application.utils.TimeUtils;
import com.application.utils.excel.DynamicExcelData;
import com.application.utils.excel.ExcelDynamicExport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @PostMapping("/upload")
    public String upload(HttpServletRequest request){
        //System.out.println("1:"+TimeUtils.getNowTime());
        String fileName = request.getHeader("filename");
        File targetFile = new File("d:\\upload2", fileName);
        //System.out.println("1:"+TimeUtils.getNowTime());
        // 使用FileChannel直接写入磁盘
        try {
            InputStream is = request.getInputStream();

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
            System.out.println("1:"+TimeUtils.getNowTime());
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
    public ResponseEntity<String> upload2(@RequestParam(value = "file") MultipartFile file){
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
    public ResponseEntity<String> upload3(@RequestParam(value = "file") MultipartFile file){
        CommonsMultipartResolver multipartResolver = (CommonsMultipartResolver) BeanUtils.getBean("multipartResolver");
        System.out.println("size:"+multipartResolver.getFileItemFactory().getSizeThreshold());
        multipartResolver.setMaxUploadSize(10*1024*1024);
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
    public ResponseEntity<String> upload4(HttpServletRequest request,HttpServletResponse response){


        return ResponseEntity.ok("File uploaded successfully");
    }
}