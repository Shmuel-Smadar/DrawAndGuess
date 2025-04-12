package com.example.drawandguess.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.drawandguess.config.APIConstants.VM_CONNECTOR;

@Configuration
public class EmbeddedActiveMQConfig {
    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector(VM_CONNECTOR);
        return broker;
    }
}
