package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
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
        Long accountIdLong = null;
        if (accountId != null) {
            accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
            if (accountIdLong <= 0) {
                return "redirect:/account_transaction/index?error=Invalid+input+parameters";
            }
        }

        try {
            if(accountIdLong != null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));

                var transactionDtoList = _transactionService.loadLastAccountTransactions(accountIdLong);
                model.addAttribute("transactionDtoList", transactionDtoList);
            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("transactionDtoList", new ArrayList<TransactionDto>());
            }

            return "account_transaction";
        } catch (BusinessException ex) {
            return "redirect:/account_transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/account_transaction/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account_transaction/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/account_transaction/index";
        }

        return "redirect:/account_transaction/index?account_id=" + accountId;
    }
}
