package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.AccountService;
import com.tosan.exceptions.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bank_account")
@Layout(title = "Bank Accounts", value = "layouts/default")
public class BankAccountController {
    private final AccountService _accountService;

    public BankAccountController(AccountService accountService) {
        _accountService = accountService;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        try {
            var bankAccountsList = _accountService.loadBankAccounts();
            model.addAttribute("accountDtoList", bankAccountsList);

            return "bank_account";
        } catch (BusinessException ex) {
            return "redirect:/branch_transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/branch_transaction/index?error=unhandled+error+occurred";
        }
    }
}
