package com.example.drawandguess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.drawandguess.model.WordOptions;
import java.util.List;
import java.util.Random;

@Service
public class WordService {

    private final JdbcTemplate jdbcTemplate;
    private final boolean useDatabase;
    private final List<String> defaultWords = List.of(
            "Cat", "Computer", "Pizza", "Bicycle", "Tree", "Car", "House", "Sun", "Moon", "Banana"
    );

    public WordService(JdbcTemplate jdbcTemplate, @Value("${USE_DB:false}") boolean useDatabase) {
        this.jdbcTemplate = jdbcTemplate;
        this.useDatabase = useDatabase;
    }

    public WordOptions getRandomWords() {
        if (useDatabase) {
            List<String> words = jdbcTemplate.queryForList(
                    "SELECT english_word FROM Words ORDER BY RANDOM() LIMIT 3",
                    String.class
            );
            while (words.size() < 3) {
                words.add(getRandomDefaultWord());
            }
            return new WordOptions(words.get(0), words.get(1), words.get(2));
        } else {
            return new WordOptions(getRandomDefaultWord(), getRandomDefaultWord(), getRandomDefaultWord());
        }
    }

    private String getRandomDefaultWord() {
        Random r = new Random();
        return defaultWords.get(r.nextInt(defaultWords.size()));
    }
}
