package com.example.drawandguess.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import static com.example.drawandguess.config.PathConstants.topicRoomDrawing;
import static com.example.drawandguess.config.PathConstants.topicRoomClearCanvas;

@Service
public class DrawingService {
    private final SimpMessagingTemplate messagingTemplate;

    public DrawingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void draw(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend(topicRoomDrawing(roomId), message);
    }

    public void clearCanvas(String roomId, ClearCanvasMessage message) {
        messagingTemplate.convertAndSend(topicRoomClearCanvas(roomId), message);
    }
}
