package com.example.drawandguess.controller;

import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import com.example.drawandguess.service.DrawingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import static com.example.drawandguess.config.APIConstants.DRAW_MAPPING;
import static com.example.drawandguess.config.APIConstants.CLEAR_CANVAS_MAPPING;

@Controller
public class DrawController {
    private static final Logger logger = LoggerFactory.getLogger(DrawController.class);
    private final DrawingService drawingService;

    public DrawController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }

    // A method that passes a drawing message from the drawer to draw on the canvas of the users in that room
    @MessageMapping(DRAW_MAPPING)
    public void draw(@DestinationVariable String roomId, DrawMessage message) {
        try {
            drawingService.draw(roomId, message);
        } catch (Exception e) {
            logger.error("Error in draw", e);
        }
    }
    // A method that passes a clear canvas message from the drawer to the other users in that room
    @MessageMapping(CLEAR_CANVAS_MAPPING)
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        try {
            drawingService.clearCanvas(roomId, message);
        } catch (Exception e) {
            logger.error("Error in clearCanvas", e);
        }
    }
}
