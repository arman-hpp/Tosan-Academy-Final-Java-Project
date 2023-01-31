package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.services.*;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.utils.*;
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
    public String accountForm(@RequestParam(required = false) String accountId,
                               @RequestParam(required = false, name = "customer_id") String customerid,
                               Model model) {
        try {
            if(customerid != null) {
                var customerIdLong = ConvertorUtils.tryParseLong(customerid, -1L);
                if(customerIdLong <= 0) {
                    return "redirect:/account/index?error=Invalid+input+parameters";
                }

                var foundCustomer = _customerService.loadCustomer(customerIdLong);
                model.addAttribute("customerOutputs", foundCustomer);
            }
            else if(accountId != null) {
                var idLong = ConvertorUtils.tryParseLong(accountId, -1L);
                if(idLong <= 0) {
                    return "redirect:/account/index?error=Invalid+input+parameters";
                }

                var foundAccount = _accountService.loadAccount(idLong);
                model.addAttribute("accountOutputs", foundAccount);
            }

            return "account";
        } catch (BusinessException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchCustomer")
    public String searchCustomerSubmit(@ModelAttribute CustomerSearchInputDto searchCustomerInputsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=Invalid+input+parameters";
        }

        var customerId = searchCustomerInputsDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/account/index";
        }

        return "redirect:/account/index?customer_id=" + customerId;
    }

    @PostMapping("/searchCustomer")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        var customerId = accountSearchInputDto.getCustomerId();

        if (accountId == null && customerId == null) {
            return "redirect:/account/index";
        }

        if (customerId != null) {
            var searchLink =  "redirect:/account/index?customer_id=" + customerId;
        }

        return "account";
    }
}
