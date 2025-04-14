package com.example.drawandguess.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Collections;

import static com.example.drawandguess.config.APIConstants.VM_CONNECTOR;

@Configuration
public class ActiveMQConfig {

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(VM_CONNECTOR);
        connectionFactory.setTrustedPackages(Collections.singletonList("com.example.drawandguess.model"));
        return connectionFactory;
    }
}
