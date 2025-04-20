package com.application.upload;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class UploadProgressWebSocketHandler extends TextWebSocketHandler {

    // 存储 WebSocket 会话，用于向特定用户推送消息
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后，将会话存入 Map（使用用户 ID 作为 key）
        String userId = session.getUri().getQuery(); // 假设用户 ID 通过 WebSocket URL 参数传递
        sessions.put(userId, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 移除关闭的会话
        sessions.values().remove(session);
    }

    public static void sendProgress(String userId, String progressMessage) throws IOException {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new org.springframework.web.socket.TextMessage(progressMessage));
        }
    }
}
