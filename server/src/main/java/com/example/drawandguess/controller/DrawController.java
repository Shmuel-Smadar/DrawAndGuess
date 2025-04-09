package com.example.drawandguess.controller;

import static com.example.drawandguess.config.PathConstants.DRAW_MAPPING;
import static com.example.drawandguess.config.PathConstants.CLEAR_CANVAS_MAPPING;

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
    @MessageMapping(DRAW_MAPPING)
    public void draw(@DestinationVariable String roomId, DrawMessage message) {
        drawingService.draw(roomId, message);
    }
    @MessageMapping(CLEAR_CANVAS_MAPPING)
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        drawingService.clearCanvas(roomId, message);
    }
}
