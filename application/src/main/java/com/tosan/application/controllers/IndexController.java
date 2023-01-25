package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@Layout(title = "Test", value = "layouts/default")
public class IndexController {
    @GetMapping({"/", "/index"})
    @Layout(value = Layout.NONE)
    public String index() {
        return "index";
    }

    @GetMapping("home")
    public String home() {
        return "home";
    }
}