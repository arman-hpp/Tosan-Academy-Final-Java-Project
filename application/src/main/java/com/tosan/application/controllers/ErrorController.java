package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errors")
@Layout(Layout.NONE)
public class ErrorController {
    @GetMapping("/error-403")
    public String handleError403() {
        return "/errors/error-403";
    }

    @GetMapping("/error-404")
    public String handleError404() {
        return "/errors/error-404";
    }

    @GetMapping("/error-500")
    public String handleError500() {
        return "/errors/error-500";
    }
}
