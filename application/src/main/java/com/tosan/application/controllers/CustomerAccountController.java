package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountDto;
import com.tosan.core_banking.dtos.CustomerSearchInputDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/customer_account")
@Layout(title = "Customer Accounts", value = "layouts/default")
public class CustomerAccountController {
    private final AccountService _accountService;

    public CustomerAccountController(AccountService accountService) {
        _accountService = accountService;
    }


    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "customer_id", required = false) String customerId,
                           Model model) {
        try {
            Long customerIdLong = null;
            if (customerId != null) {
                customerIdLong = ConvertorUtils.tryParseLong(customerId, -1L);
                if (customerIdLong <= 0) {
                    return "redirect:/customer_account/index?error=Invalid+input+parameters";
                }
            }

            if (customerIdLong == null) {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());
                model.addAttribute("accountDtoList", new ArrayList<AccountDto>());
            } else {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto(customerIdLong));
                var customerAccountsList = _accountService.loadCustomerAccount(customerIdLong);
                model.addAttribute("accountDtoList", customerAccountsList);
            }

            return "customer_account";
        } catch (BusinessException ex) {
            return "redirect:/customer_account/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer_account/index?error=unhandled+error+occurred";
        }
    }


    @PostMapping("/searchCustomer")
    public String searchCustomerSubmit(@ModelAttribute CustomerSearchInputDto customerSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/customer_account/index?error=Invalid+input+parameters";
        }

        var customerId = customerSearchInputDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/customer_account/index";
        }

        return "redirect:/customer_account/index?customer_id=" + customerId;
    }
}
