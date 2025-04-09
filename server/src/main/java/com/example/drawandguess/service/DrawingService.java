package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
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

    public void draw(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend(Constants.topicRoomDrawing(roomId), message);
    }

    public void clearCanvas(String roomId, ClearCanvasMessage message) {
        messagingTemplate.convertAndSend(Constants.topicRoomClearCanvas(roomId), message);
    }
}
