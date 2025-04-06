package com.example.drawandguess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.drawandguess.model.WordOptions;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;

@Service
public class WordService {

    private final JdbcTemplate jdbcTemplate;
    private final boolean useDatabase;
    private final List<String> defaultWordPairs = List.of(
            "Cat : חתול",
            "Computer : מחשב",
            "Pizza : פיצה",
            "Bicycle : אופניים",
            "Tree : עץ",
            "Car : מכונית",
            "House : בית",
            "Sun : שמש",
            "Moon : ירח",
            "Banana : בננה"
    );

    public WordService(JdbcTemplate jdbcTemplate, @Value("${USE_DB:false}") boolean useDatabase) {
        this.jdbcTemplate = jdbcTemplate;
        this.useDatabase = useDatabase;
    }

    public WordOptions getRandomWords() {
        if (useDatabase) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT english_word, hebrew_word FROM words ORDER BY RANDOM() LIMIT 3"
            );
            List<String> wordPairs = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                String english = (String) row.get("english_word");
                String hebrew = (String) row.get("hebrew_word");
                wordPairs.add(english + " : " + hebrew);
            }
            while (wordPairs.size() < 3) {
                wordPairs.add(getRandomDefaultWordPair());
            }
            return new WordOptions(wordPairs.get(0), wordPairs.get(1), wordPairs.get(2));
        } else {
            List<String> wordPairs = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                wordPairs.add(getRandomDefaultWordPair());
            }
            return new WordOptions(wordPairs.get(0), wordPairs.get(1), wordPairs.get(2));
        }
    }

    private String getRandomDefaultWordPair() {
        Random r = new Random();
        return defaultWordPairs.get(r.nextInt(defaultWordPairs.size()));
    }
}