package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountDto;
import com.tosan.core_banking.dtos.CustomerSearchInputDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.CustomerService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
@Layout(title = "Accounts", value = "layouts/default")
public class AccountController {
    private final AccountService _accountService;
    private final CustomerService _customerService;

    public AccountController(AccountService accountService, CustomerService customerService) {
        _accountService = accountService;
        _customerService = customerService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "customer_id", required = false) String customerId,
                           Model model) {
        var customerIdLong = ConvertorUtils.tryParseLong(customerId, null);

        try {
            var accountDtoList = _accountService.loadAccounts();
            model.addAttribute("accountDtoList", accountDtoList);

            if (customerIdLong == null) {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());
                model.addAttribute("accountDto", new AccountDto());
            } else {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto(customerIdLong));

                var customerDto = _customerService.loadCustomer(customerIdLong);
                var accountDto = new AccountDto();
                accountDto.setCustomerId(customerIdLong);
                accountDto.setCustomerName(customerDto.getFullName());
                model.addAttribute("accountDto", accountDto);
            }

            return "account";
        } catch (Exception ex) {
            return "redirect:/account/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchCustomer")
    public String searchCustomerSubmit(@ModelAttribute CustomerSearchInputDto customerSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var customerId = customerSearchInputDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/account/index";
        }

        return "redirect:/account/index?customer_id=" + customerId;
    }

    @PostMapping("/addAccount")
    public String addSubmit(@ModelAttribute AccountDto accountDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            _accountService.addAccount(accountDto);

            return "redirect:/account/index";
        } catch (Exception ex) {
            return "redirect:/account/index?error=" + ControllerErrorParser.getError(ex);
        }
    }
}
