package com.application.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/***
 * @author mazaiting
 * @date 2019-06-27
 * @decription HttpServletRequest 包装器
 * 解决: request.getInputStream()只能读取一次的问题
 * 目标: 流可重复读
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    /**
     * 日志
     */
    private static final Logger mLogger = LoggerFactory.getLogger(RequestWrapper.class);

    /**
     * 请求体
     */
    private String mBody;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        // 将body数据存储起来
        mBody = getBody(request);
    }

    /**
     * 获取请求体
     * @param request 请求
     * @return 请求体
     */
    private String getBody(HttpServletRequest request) {
        try {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            return new String(bodyBytes, request.getCharacterEncoding());
        } catch (IOException e) {
            mLogger.debug(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取请求体
     * @return 请求体
     */
    public String getBody() {
        return mBody;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 创建字节数组输入流
        final ByteArrayInputStream bais = new ByteArrayInputStream(mBody.getBytes(Charset.defaultCharset()));

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}

