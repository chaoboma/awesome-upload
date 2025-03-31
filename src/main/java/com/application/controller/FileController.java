package com.application.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.application.common.Result;
import com.application.domain.dao.req.TestInterceptorReq;
import com.application.utils.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
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
}