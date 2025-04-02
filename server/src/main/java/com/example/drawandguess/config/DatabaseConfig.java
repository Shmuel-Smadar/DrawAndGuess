package com.example.drawandguess.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DB_HOST:localhost}")
    private String host;

    @Value("${DB_PORT:5432}")
    private String port;

    @Value("${DB_NAME:postgres}")
    private String dbName;

    @Value("${DB_USER:postgres}")
    private String user;

    @Value("${DB_PASS:1}")
    private String pass;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
