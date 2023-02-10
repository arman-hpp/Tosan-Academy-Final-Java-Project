package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserRegisterInputDto;
import com.tosan.core_banking.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(("/register"))
@Layout(title = "Register", value = "layouts/public")
public class RegisterController {
    private final UserService _userService;

    public RegisterController(UserService _userService) {
        this._userService = _userService;
    }

    @GetMapping("/index")
    public String registerForm(Model model) {
        model.addAttribute("userRegisterInputDto", new UserRegisterInputDto());
        return "register";
    }

    @PostMapping("/addUser")
    public String registerSubmit(@ModelAttribute UserRegisterInputDto userRegisterInputDto, BindingResult bindingResult) {
        try {
            if(!userRegisterInputDto.getPassword().equals(userRegisterInputDto.getRepeatPassword())) {
                ControllerErrorParser.setPasswordMisMatchedError(bindingResult);
                return "register";
            }

            _userService.register(userRegisterInputDto);

            return "redirect:/auth/index";
        } catch (Exception ex) {
            return "redirect:/register/index?error=" + ControllerErrorParser.getError(ex);
        }
    }
}
