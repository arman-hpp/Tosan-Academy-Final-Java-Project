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
    public String accountForm(@RequestParam(name = "account_id") String accountId,
                               @RequestParam(name = "customer_id") String customerId,
                               Model model) {
        try {
            Long customerIdLong = null;
            if(customerId != null) {
                customerIdLong = ConvertorUtils.tryParseLong(customerId, -1L);
                if(customerIdLong <= 0) {
                    return "redirect:/account/index?error=Invalid+input+parameters";
                }
            }

            Long accountIdLong = null;
            if(accountId != null) {
                accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
                if (accountIdLong <= 0) {
                    return "redirect:/account/index?error=Invalid+input+parameters";
                }
            }

            var accounts = _accountService.searchAccounts(accountIdLong, customerIdLong);
            model.addAttribute("accountOutputs", accounts);

            return "account";
        } catch (BusinessException ex) {
            return "redirect:/account/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/account/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/index")
    public String accountFormByCustomerId(@RequestParam(name = "customer_id") String customerId, Model model) {
        try {
            var customerIdLong = ConvertorUtils.tryParseLong(customerId, -1L);
            if (customerIdLong <= 0) {
                return "redirect:/account/index?error=Invalid+input+parameters";
            }

            var foundCustomer = _customerService.loadCustomer(customerIdLong);
            model.addAttribute("customerOutputs", foundCustomer);

            var accounts = _accountService.loadAccounts();
            model.addAttribute("accountOutputs", accounts);

            return "account";

        } catch (BusinessException ex) {
            return "redirect:/account/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/account/index?error=unhandled+error+occurred";
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

    @PostMapping("/addAccount")
    public String addAccountSubmit(@ModelAttribute AccountDto accountDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=Invalid+input+parameters";
        }

        try {
            _accountService.addAccount(accountDto);

            return "redirect:/account/index";
        } catch (BusinessException ex) {
            return "redirect:/account/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/account/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        var customerId = accountSearchInputDto.getCustomerId();

        var queryParams = "?account_id=" + accountId + "&customer_id=" + customerId;

        return "redirect:/account/index".concat(queryParams);
    }
}
