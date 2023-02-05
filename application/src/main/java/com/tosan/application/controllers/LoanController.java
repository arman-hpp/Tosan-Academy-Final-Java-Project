package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.loan.services.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loan")
@Layout(title = "Loans", value = "layouts/default")
public class LoanController {
    private final LoanService _loanService;

    public LoanController(LoanService loanService) {
        _loanService = loanService;
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/loan/index";
        }

        return "redirect:/loan/index?account_id=" + accountId;
    }
}
