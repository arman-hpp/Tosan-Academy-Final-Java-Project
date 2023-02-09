package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/account_transaction")
@Layout(title = "Account Transactions", value = "layouts/default")
public class AccountTransactionController {
    private final TransactionService _transactionService;

    public AccountTransactionController(TransactionService transactionService) {
        _transactionService = transactionService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "account_id", required = false) String accountId,
                           Model model) {
        var accountIdLong = ConvertorUtils.tryParseLong(accountId, null);

        try {
            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("transactionDtoList", new ArrayList<TransactionDto>());
            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));
                var transactionDtoList = _transactionService.loadLastAccountTransactions(accountIdLong);
                model.addAttribute("transactionDtoList", transactionDtoList);
            }

            return "account_transaction";
        } catch (Exception ex) {
            return "redirect:/account_transaction/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account_transaction/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/account_transaction/index";
        }

        return "redirect:/account_transaction/index?account_id=" + accountId;
    }
}
