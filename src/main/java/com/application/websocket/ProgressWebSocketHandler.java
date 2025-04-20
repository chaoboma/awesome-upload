package com.application.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/progress/{uploadId}")
@Component
public class ProgressWebSocketHandler extends TextWebSocketHandler {
    // 存储所有活跃会话（Key: uploadId, Value: WebSocket Session）
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("uploadId") String uploadId) {
        sessions.put(uploadId, session);
    }

    @OnClose
    public void onClose(@PathParam("uploadId") String uploadId) {
        sessions.remove(uploadId);
    }

    public static void sendProgress(String uploadId, String message) {
        Session session = sessions.get(uploadId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }
}

