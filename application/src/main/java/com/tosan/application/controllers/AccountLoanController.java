package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.services.LoanService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/account_loan")
@Layout(title = "Account Loans", value = "layouts/default")
public class AccountLoanController {
    private final LoanService _loanService;

    public AccountLoanController(LoanService loanService) {
        _loanService = loanService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "account_id", required = false) String accountId,
                           Model model) {
        Long accountIdLong = null;
        if (accountId != null) {
            accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
            if (accountIdLong <= 0) {
                return "redirect:/account_loan/index?error=Invalid+input+parameters";
            }
        }

        try {
            if(accountIdLong != null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));

                var loanDtoList = _loanService.loadLoansByAccountId(accountIdLong);
                model.addAttribute("loanDtoList", loanDtoList);
            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("loanDtoList", new ArrayList<LoanDto>());
            }

            return "account_loan";
        } catch (BusinessException ex) {
            return "redirect:/account_loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/account_loan/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account_loan/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/account_loan/index";
        }

        return "redirect:/account_loan/index?account_id=" + accountId;
    }
}
