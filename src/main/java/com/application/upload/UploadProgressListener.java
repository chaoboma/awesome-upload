package com.application.upload;
import com.application.websocket.ProgressWebSocketHandler;
import org.apache.commons.fileupload.ProgressListener;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
public class UploadProgressListener implements ProgressListener {
    // 线程本地变量存储 uploadId（线程安全）
    private static final ThreadLocal<String> currentUploadId = new ThreadLocal<>();

    @Override
    public void update(long bytesRead, long contentLength, int items) {
        String uploadId = currentUploadId.get();
        if (uploadId != null) {
            // 计算进度百分比
            double progress = (bytesRead * 100.0) / contentLength;
            String message = String.format("{\"uploadId\": \"%s\", \"progress\": %.2f}", uploadId, progress);

            // 通过 WebSocket 发送进度
            ProgressWebSocketHandler.sendProgress(uploadId, message);
        }
    }

    public static void setCurrentUploadId(String uploadId) {
        currentUploadId.set(uploadId);
    }

    public static void clear() {
        currentUploadId.remove();
    }
}
