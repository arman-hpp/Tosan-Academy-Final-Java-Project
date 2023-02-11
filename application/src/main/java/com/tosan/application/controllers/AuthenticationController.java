package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(("/auth"))
@Layout(title = "Login", value = "layouts/public")
public class AuthenticationController {
    public AuthenticationController() {
    }

    @GetMapping( "/login")
    public String loginForm(@RequestParam(required = false) String username, Model model) {
        if(username == null) {
            model.addAttribute("loginInputDto", new UserLoginInputDto());
        } else {
            model.addAttribute("loginInputDto", new UserLoginInputDto(username, null));
        }

        return "views/public/login";
    }
}
