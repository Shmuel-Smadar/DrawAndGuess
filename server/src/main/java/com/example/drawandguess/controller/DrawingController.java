package com.example.drawandguess.controller;
import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DrawingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/{roomId}/startDrawing")
    public void startDrawing(@DestinationVariable String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }

    @MessageMapping("/room/{roomId}/draw")
    public void draw(@DestinationVariable String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }

    @MessageMapping("/room/{roomId}/stopDrawing")
    public void stopDrawing(@DestinationVariable String roomId, DrawMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/drawing", message);
    }

    @MessageMapping("/room/{roomId}/clearCanvas")
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/clearCanvas", message);
    }
}
