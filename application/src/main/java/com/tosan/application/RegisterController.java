package com.tosan.application;

import com.tosan.core_banking.services.UserService;
import com.tosan.core_banking.dtos.RegisterInputDto;
import com.tosan.core_banking.exceptions.BankException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("/user")
public class RegisterController {
    private final UserService _userService;

    public RegisterController(UserService _userService) {
        this._userService = _userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerInputDto", new RegisterInputDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute RegisterInputDto registerInputDto, Model model) {
        try {
            _userService.register(registerInputDto);
            return "redirect:/login";
        }
        catch (BankException ex) {
            return "redirect:/register?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/register?error=unhandled+error+occurred";
        }
    }
}
