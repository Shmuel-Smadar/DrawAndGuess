package com.example.drawandguess.config;

import com.example.drawandguess.handler.ExampleWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register WebSocket handler at the specified endpoint
        registry.addHandler(new ExampleWebSocketHandler(), "/example")
                .setAllowedOrigins("*"); // Allow all origins for simplicity; adjust as needed
    }

}
