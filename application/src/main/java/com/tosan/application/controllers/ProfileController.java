package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@Layout(title = "Profile", value = "layouts/default")
public class ProfileController {
    private final AuthenticationService _authenticationService;

    public ProfileController(AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        var userDto = _authenticationService.loadCurrentUser();
        model.addAttribute("userDto", userDto);

        return "profile";
    }
}
