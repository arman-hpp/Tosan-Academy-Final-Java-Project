package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.UserService;
import com.tosan.core_banking.dtos.UserLoginInputDto;
import com.tosan.exceptions.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Layout(value = Layout.NONE)
@RequestMapping(("/auth"))
public class LoginController {
    private final UserService _userService;

    public LoginController(UserService _userService) {
        this._userService = _userService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginInputDto", new UserLoginInputDto());

        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute UserLoginInputDto loginInputDto) {
        try {
            _userService.login(loginInputDto);

            return "redirect:/home";
        }
        catch (BusinessException ex) {
            return "redirect:/login?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/login?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/index";
    }
}
