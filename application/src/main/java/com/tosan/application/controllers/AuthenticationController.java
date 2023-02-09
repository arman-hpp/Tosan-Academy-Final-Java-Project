package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.UserService;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import com.tosan.exceptions.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/authenticate")
    public String loginSubmit(@ModelAttribute UserLoginInputDto loginInputDto) {
        try {
            _userService.login(loginInputDto);

            return "redirect:/home";
        }
        catch (BusinessException ex) {
            return "redirect:/auth/index?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/auth/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/index";
    }
}
