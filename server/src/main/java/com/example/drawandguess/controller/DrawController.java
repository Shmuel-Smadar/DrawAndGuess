package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import com.example.drawandguess.service.DrawingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {
    private final DrawingService drawingService;
    public DrawController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }
    @MessageMapping(Constants.DRAW_MAPPING)
    public void draw(@DestinationVariable String roomId, DrawMessage message) {
        drawingService.draw(roomId, message);
    }
    @MessageMapping(Constants.CLEAR_CANVAS_MAPPING)
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        drawingService.clearCanvas(roomId, message);
    }
}
