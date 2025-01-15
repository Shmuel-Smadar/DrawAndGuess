package com.example.drawandguess.controller;

import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import com.example.drawandguess.service.DrawingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DrawingController {
    private final DrawingService drawingService;
    public DrawingController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }
    @MessageMapping("/room/{roomId}/startDrawing")
    public void startDrawing(@DestinationVariable String roomId, DrawMessage message) {
        drawingService.startDrawing(roomId, message);
    }
    @MessageMapping("/room/{roomId}/draw")
    public void draw(@DestinationVariable String roomId, DrawMessage message) {
        drawingService.draw(roomId, message);
    }
    @MessageMapping("/room/{roomId}/stopDrawing")
    public void stopDrawing(@DestinationVariable String roomId, DrawMessage message) {
        drawingService.stopDrawing(roomId, message);
    }
    @MessageMapping("/room/{roomId}/clearCanvas")
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        drawingService.clearCanvas(roomId, message);
    }
}
