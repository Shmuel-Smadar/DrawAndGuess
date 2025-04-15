package com.example.drawandguess.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;

import static com.example.drawandguess.config.APIConstants.topicRoomDrawing;
import static com.example.drawandguess.config.APIConstants.topicRoomClearCanvas;

/*
 * Service that broadcasts drawing events and clear-canvas events
 * to all subscribers in a given room.
 */
@Service
public class DrawingService {
    private final SimpMessagingTemplate messagingTemplate;

    public DrawingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    
    // Sends a draw message (containing coordinates, color, eventType) to everyone in the room.
    public void draw(String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend(topicRoomDrawing(roomId), message);
    }

    
    // Sends a clear-canvas message to everyone in the room.
    public void clearCanvas(String roomId, ClearCanvasMessage message) {
        messagingTemplate.convertAndSend(topicRoomClearCanvas(roomId), message);
    }
}
