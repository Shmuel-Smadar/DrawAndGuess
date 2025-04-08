package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DrawAndGuessController {

    @RequestMapping({Constants.DRAW_AND_GUESS_PATH, Constants.DRAW_AND_GUESS_SLASH_PATH, Constants.HOME_PATH})
    public String forwardToIndex() {
        return Constants.MAIN_INDEX_FILE;
    }
}
