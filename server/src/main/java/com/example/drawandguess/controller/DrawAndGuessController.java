package com.example.drawandguess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DrawAndGuessController {

    @RequestMapping({"/new/drawandguess", "/new/drawandguess/"})
    public String forwardToIndex() {
        return "forward:/new/drawandguess/index.html";
    }
}
