package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.BindingResultHelper;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.UserService;
import com.tosan.core_banking.dtos.*;
import com.tosan.exceptions.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
                BindingResultHelper.addGlobalError(bindingResult, "the password not matched");
                return "register";
            }

            _userService.register(userRegisterInputDto);

            return "redirect:/login/index";
        }
        catch (BusinessException ex) {
            return "redirect:/register/index?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/register/index?error=unhandled+error+occurred";
        }
    }
}
