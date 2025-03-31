package com.application.Interceptor;


import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.stream.Collectors;

/**
 * HandlerInterceptorAdapter类在 Spring 5.3之后就过时了，推荐使用 HandlerInterceptor类
 */
@Slf4j
@Component
public class ApiSignInterceptor implements HandlerInterceptor {

    /**
     * timestamp过期时间，单位：毫秒
     */
    private final static Long TIMESTAMP_EXPIRE_TIME = 1000 * 60 * 5L;
    public static Integer timeout = 300000;
    public static ExpiringCache<String, String> cache = new ExpiringCache<>(300000); // 超时时间
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try{
            String sign = request.getHeader("sign");
            if(sign == null || sign.equals("")){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("sign empty");
                return false;
            }
            String timestamp = request.getHeader("timestamp");
            if(timestamp == null || timestamp.equals("")){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("timestamp empty");
                return false;
            }
            String salt = "241rew";
            String signBackend = DigestUtils.md5Hex(timestamp+salt);

            log.debug("timestamp+salt:"+timestamp+salt);
            log.debug("signBackend:"+signBackend);
            if (!sign.equals(signBackend)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("timestamp invalid");
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }




        return true;
    }







}

