package com.example.drawandguess.controller;

import static com.example.drawandguess.config.APIConstants.DRAW_AND_GUESS_PATH;
import static com.example.drawandguess.config.APIConstants.DRAW_AND_GUESS_SLASH_PATH;
import static com.example.drawandguess.config.APIConstants.HOME_PATH;
import static com.example.drawandguess.config.APIConstants.MAIN_INDEX_FILE;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DrawAndGuessController {
    @RequestMapping({DRAW_AND_GUESS_PATH, DRAW_AND_GUESS_SLASH_PATH, HOME_PATH})
    public String forwardToIndex() {
        return MAIN_INDEX_FILE;
    }
}
