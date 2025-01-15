package com.example.drawandguess.service;

import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DrawingService {
    private final SimpMessagingTemplate messagingTemplate;
    public DrawingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void startDrawing(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }
    public void draw(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }
    public void stopDrawing(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }
    public void clearCanvas(String roomId, ClearCanvasMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/clearCanvas", message);
    }
}
