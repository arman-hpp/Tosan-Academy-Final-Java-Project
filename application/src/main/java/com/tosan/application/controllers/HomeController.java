package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
@Layout(title = "Home", value = "layouts/default")
public class HomeController {
    @GetMapping({"/", "index"})
    public String home() {
        return "home";
    }
}
