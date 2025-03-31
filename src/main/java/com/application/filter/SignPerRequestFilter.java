package com.application.filter;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author
 */
@Slf4j
public class SignPerRequestFilter extends OncePerRequestFilter {
    public static  Integer timeout = 2000;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try{
            String encodedBody = "";
            String body = "";
            //log.debug("timeout:"+timeout);
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                try{
                    body = readRequestBody(request);
                }catch (Exception e){

                }
                if (!body.equals("") && body !=null) {
                    JSONObject jsonObject = null;
                    String bodyStr = "";

                    try{
                        jsonObject = new JSONObject(body);
                        if(jsonObject != null){
                            bodyStr = jsonObject.toString();

                        }
                    }catch(Exception e){

                    }
                    if(bodyStr.equals("")){
                        bodyStr = body;
                    }

                    try{
                        if(!bodyStr.equals("")){
                            encodedBody = URLEncoder.encode(bodyStr, "UTF-8");
                        }

                    }catch(Exception e){

                    }
                }
                String sign = request.getHeader("sign");
                if(sign == null || sign.equals("")){
                    ((HttpServletResponse) response).sendError(400, "sign empty");
                    return;
                }
                log.debug("sign:"+sign);
                String salt = "241rew";
                String signBackend = DigestUtils.md5Hex(encodedBody + salt);
                log.debug("signBackend:"+signBackend);
                if (!sign.equals(signBackend)) {
                    ((HttpServletResponse) response).sendError(400, "sign invalid");
                    return;
                }
                // 使用自定义的请求包装器替换原始请求
                filterChain.doFilter(new SignHttpServletRequestWrapper(request, body), response);

            } else {
                // 对于非POST请求，继续过滤器链
                filterChain.doFilter(request, response);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    // 自定义的请求包装器
    static class SignHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public SignHttpServletRequestWrapper(HttpServletRequest request, String body) {
            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes("UTF-8"));
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
                public void setReadListener(ReadListener listener) {

                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }
}
