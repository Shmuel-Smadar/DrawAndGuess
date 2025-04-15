package com.example.drawandguess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

/*
 * Manages database connectivity settings and ensures necessary tables exist
 * if USE_DB (environment variable) is true.
 */
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

    @Value("${USE_DB:false}")
    private boolean useDatabase;

    private JdbcTemplate jdbcTemplate;

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
        this.jdbcTemplate = new JdbcTemplate(ds);
        return this.jdbcTemplate;
    }

    // A function that ensures the required tables exist in the database, if not - creates them.
    @EventListener(ApplicationReadyEvent.class)
    public void createTablesIfNotExists() {
        if (useDatabase && jdbcTemplate != null) {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS leaderboard (username VARCHAR(255) PRIMARY KEY, score INT NOT NULL, message VARCHAR(255))");
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS words (english_word VARCHAR(255), hebrew_word VARCHAR(255))");
        }
    }
}