package com.example.drawandguess.controller;

import java.io.FileWriter;
import java.io.IOException;
import static com.example.drawandguess.config.APIConstants.DRAW_MAPPING;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import static com.example.drawandguess.config.APIConstants.CLEAR_CANVAS_MAPPING;
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
        try {
            drawingService.draw(roomId, message);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in draw: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
    @MessageMapping(CLEAR_CANVAS_MAPPING)
    public void clearCanvas(@DestinationVariable String roomId, ClearCanvasMessage message) {
        try {
            drawingService.clearCanvas(roomId, message);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in clearCanvas: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
}
