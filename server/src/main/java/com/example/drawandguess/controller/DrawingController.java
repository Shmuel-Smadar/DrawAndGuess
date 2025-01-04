package com.example.drawandguess.controller;
import com.example.drawandguess.model.DrawMessage;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;



@Controller
public class DrawingController {

    // Called when the client starts drawing
    @MessageMapping("/startDrawing")
    @SendTo("/topic/drawing")
    public DrawMessage startDrawing(DrawMessage message) {
        message.setEventType("START");
        return message;
    }

    // Called repeatedly as the user drags the mouse or finger
    @MessageMapping("/draw")
    @SendTo("/topic/drawing")
    public DrawMessage draw(DrawMessage message) {
        message.setEventType("DRAW");
        return message;
    }

    // Called when the client stops drawing
    @MessageMapping("/stopDrawing")
    @SendTo("/topic/drawing")
    public DrawMessage stopDrawing(DrawMessage message) {
        message.setEventType("STOP");
        return message;
    }
    @MessageMapping("/clearCanvas")
    @SendTo("/topic/clearCanvas")
    public ClearCanvasMessage clearCanvas(ClearCanvasMessage message) {
        return message;
    }
}
