package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@Layout(title = "Index", value = "layouts/public")
public class IndexController {
    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/auth/index";
    }
}