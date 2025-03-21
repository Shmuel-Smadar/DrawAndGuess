package com.example.drawandguess.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.example.drawandguess.service.GameService;

@Component
public class WebSocketEventListener {
    private final GameService gameService;

    @Autowired
    public WebSocketEventListener(GameService gameService) {
        this.gameService = gameService;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        gameService.handleDisconnect(sessionId);
    }
}
