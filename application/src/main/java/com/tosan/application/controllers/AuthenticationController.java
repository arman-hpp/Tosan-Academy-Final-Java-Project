package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import com.tosan.core_banking.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(("/auth"))
@Layout(title = "Login", value = "layouts/public")
public class AuthenticationController {
    private final UserService _userService;

    public AuthenticationController(UserService _userService) {
        this._userService = _userService;
    }

    @GetMapping({"/", "/index"})
    public String loginForm(Model model) {
        model.addAttribute("loginInputDto", new UserLoginInputDto());

        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute UserLoginInputDto loginInputDto) {
        try {
            _userService.login(loginInputDto);

            return "redirect:/home";
        } catch (Exception ex) {
            return "redirect:/auth/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/index";
    }
}
