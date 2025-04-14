package com.example.drawandguess.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.example.drawandguess.service.GameLogicService;

@Component
public class WebSocketEventListener {
    private final GameLogicService gameLogicService;

    @Autowired
    public WebSocketEventListener(GameLogicService gameLogicService) {
        this.gameLogicService = gameLogicService;
    }

    // A listener which responsible for notifying gameLogicService if a user quit a game abruptly.
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        gameLogicService.handleDisconnect(sessionId);
    }
}
