package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerDefaultErrors;
import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.UserChangePasswordInputDto;
import com.tosan.core_banking.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import com.tosan.core_banking.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/change_password")
@Layout(title = "Change Password", value = "layouts/default")
public class ChangePasswordController {
    private final UserService _userService;
    private final AuthenticationService _authenticationService;

    public ChangePasswordController(UserService userService,
                                    AuthenticationService authenticationService) {
        _userService = userService;
        _authenticationService = authenticationService;
    }

    @GetMapping({"/","/index"})
    public String loadForm(Model model) {
        model.addAttribute("userChangePasswordInputDto", new UserChangePasswordInputDto());

        return "views/general/change_password";
    }

    @PostMapping("/changePassword")
    public String changePasswordSubmit(HttpServletRequest request, @ModelAttribute UserChangePasswordInputDto userChangePasswordInputDto,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/change_password/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            var userId = _authenticationService.loadCurrentUserId().orElse(null);
            if(userId == null) {
                return "redirect:/change_password/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
            }

            userChangePasswordInputDto.setId(userId);
            _userService.changePassword(userChangePasswordInputDto);
            request.logout();

            return "redirect:/auth/login";
        } catch (Exception ex) {
            return "redirect:/change_password/index?error=" + ControllerErrorParser.getError(ex);
        }
    }
}
