package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserDto;
import com.tosan.core_banking.services.UserService;
import com.tosan.exceptions.BusinessException;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@Layout(title = "Users", value = "layouts/default")
public class UserController {
    private final UserService _userService;

    public UserController(UserService userService) {
        _userService = userService;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        try {
            var users = _userService.loadUsers();
            model.addAttribute("userInputs", new UserDto());
            model.addAttribute("userOutputs", users);

            return "user";
        } catch (BusinessException ex) {
            return "redirect:/user/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/user/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=Invalid+input+parameters";
        }

        try {
            var foundUser = _userService.loadUser(idLong);
            model.addAttribute("userInputs", foundUser);

            var users = _userService.loadUsers();
            model.addAttribute("userOutputs", users);

            return "user";
        } catch (BusinessException ex) {
            return "redirect:/user/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/user/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/addUser")
    public String addSubmit(@ModelAttribute UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/index?error=Invalid+input+parameters";
        }

        try {
            _userService.addOrEditUser(userDto);

            return "redirect:/user/index";
        } catch (BusinessException ex) {
            return "redirect:/user/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/user/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=Invalid+input+parameters";
        }

        try {
            _userService.removeUser(idLong);

            return "redirect:/user/index";
        } catch (BusinessException ex) {
            return "redirect:/user/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/user/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/editUser/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=Invalid+input+parameters";
        }

        return "redirect:/user/index/" + idLong;
    }
}
