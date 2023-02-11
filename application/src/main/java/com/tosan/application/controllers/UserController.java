package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerDefaultErrors;
import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserDto;
import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.core_banking.services.UserService;
import com.tosan.utils.ConvertorUtils;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@Layout(title = "Users", value = "layouts/default")
@RolesAllowed("ROLE_ADMIN")
public class UserController {
    private final UserService _userService;
    private final AuthenticationService _authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        _userService = userService;
        _authenticationService = authenticationService;
    }

    @GetMapping({"/", "/index"})
    public String loadForm(Model model) {
        try {
            var users = _userService.loadUsers();
            model.addAttribute("userInputs", new UserDto());
            model.addAttribute("userOutputs", users);

            return "views/admin/user";
        } catch (Exception ex) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.InvalidInputParameters);
        }

        try {
            var foundUser = _userService.loadUser(idLong);
            model.addAttribute("userInputs", foundUser);

            var users = _userService.loadUsers();
            model.addAttribute("userOutputs", users);

            return "views/admin/user";
        } catch (Exception ex) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/addUser")
    public String addSubmit(@ModelAttribute UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            _userService.addOrEditUser(userDto);

            return "redirect:/user/index";
        } catch (Exception ex) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.InvalidInputParameters);
        }

        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
        }

        if (idLong.equals(currentUserId)) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
        }

        try {
            _userService.removeUser(idLong);

            return "redirect:/user/index";
        } catch (Exception ex) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/editUser/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.InvalidInputParameters);
        }

        try {
            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if (currentUserId == null) {
                return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
            }

            if (idLong.equals(currentUserId)) {
                return "redirect:/user/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
            }

            return "redirect:/user/index/" + idLong;
        } catch (Exception ex) {
            return "redirect:/user/index?error=" + ControllerErrorParser.getError(ex);
        }
    }
}
