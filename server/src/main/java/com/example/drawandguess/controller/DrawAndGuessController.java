package com.example.drawandguess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DrawAndGuessController {

    @RequestMapping({"/drawandguess", "/drawandguess/", "/home"})
    public String forwardToIndex() {
        return "forward:/drawandguess/index.html";
    }
}
