package com.example.drawandguess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import static com.example.drawandguess.config.GameConstants.NUMBER_OF_WORDS_TO_CHOOSE_FROM;
import com.example.drawandguess.model.WordOptions;
import java.util.*;

/*
 * A service that handles retrieving random word pairs for drawing, either from the database
 * or a default in-memory list (based on the configuration)
 */
@Service
public class WordService {

    private final JdbcTemplate jdbcTemplate;
    private final boolean useDatabase;
    //list of default words in case of no database.
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

    // A method that select 3 words randomly
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
            // if fewer than 3 found in DB, fill the remainder from default list
            while (wordPairs.size() < NUMBER_OF_WORDS_TO_CHOOSE_FROM) {
                wordPairs.add(getRandomDefaultWordPair());
            }
            return new WordOptions(wordPairs.get(0), wordPairs.get(1), wordPairs.get(2));
        } else {
                // Create a copy of the default list and shuffle it.
                List<String> defaultWordsShuffled = new ArrayList<>(defaultWordPairs);
                Collections.shuffle(defaultWordsShuffled);
                return new WordOptions(
                        defaultWordsShuffled.get(0),
                        defaultWordsShuffled.get(1),
                        defaultWordsShuffled.get(2)
                );
        }
    }

    private String getRandomDefaultWordPair() {
        Random r = new Random();
        return defaultWordPairs.get(r.nextInt(defaultWordPairs.size()));
    }
}