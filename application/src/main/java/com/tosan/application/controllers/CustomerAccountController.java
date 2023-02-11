package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountDto;
import com.tosan.core_banking.dtos.CustomerSearchInputDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.utils.ConvertorUtils;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/customer_account")
@Layout(title = "Customer Accounts", value = "layouts/default")
@RolesAllowed("ROLE_USER")
public class CustomerAccountController {
    private final AccountService _accountService;

    public CustomerAccountController(AccountService accountService) {
        _accountService = accountService;
    }

    @GetMapping({"/","/index"})
    public String loadForm(@RequestParam(name = "customer_id", required = false) String customerId,
                           Model model) {
        var customerIdLong = ConvertorUtils.tryParseLong(customerId, null);

        try {
            if (customerIdLong == null) {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());
                model.addAttribute("accountDtoList", new ArrayList<AccountDto>());
            } else {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto(customerIdLong));
                var accountDtoList = _accountService.loadCustomerAccounts(customerIdLong);
                model.addAttribute("accountDtoList", accountDtoList);
            }

            return "views/user/customer_account";
        } catch (Exception ex) {
            return "redirect:/customer_account/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchCustomer")
    public String searchCustomerSubmit(@ModelAttribute CustomerSearchInputDto customerSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/customer_account/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var customerId = customerSearchInputDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/customer_account/index";
        }

        return "redirect:/customer_account/index?customer_id=" + customerId;
    }
}