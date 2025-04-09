package com.example.drawandguess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import static com.example.drawandguess.config.PathConstants.APP_PREFIX;
import static com.example.drawandguess.config.PathConstants.USER_PREFIX;
import static com.example.drawandguess.config.PathConstants.TOPIC_PREFIX;
import static com.example.drawandguess.config.PathConstants.ALLOWED_ORIGINS;
import static com.example.drawandguess.config.PathConstants.STOMP_ENDPOINT;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(TOPIC_PREFIX);
        config.setApplicationDestinationPrefixes(APP_PREFIX);
        config.setUserDestinationPrefix(USER_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(STOMP_ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGINS)
                .withSockJS();
    }
}
