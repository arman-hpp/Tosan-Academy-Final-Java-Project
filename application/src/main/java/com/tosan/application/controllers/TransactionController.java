package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.services.*;

import com.tosan.exceptions.BusinessException;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transaction")
@Layout(title = "Transactions", value = "layouts/default")
public class TransactionController {
    private final TransactionService _transactionService;
    private final AccountService _accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        _transactionService = transactionService;
        _accountService = accountService;
    }
    @GetMapping("/index")
    public String loadForm(
            @RequestParam(name = "account_id", required = false) String accountId,
            Model model) {
        try {
            Long accountIdLong = null;
            if (accountId != null) {
                accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
                if (accountIdLong <= 0) {
                    return "redirect:/transaction/index?error=Invalid+input+parameters";
                }
            }

            // TODO: get userId
            var transactions = _transactionService.loadUserTransactions(1L);
            model.addAttribute("transactionOutputs", transactions);

            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputs", new AccountSearchInputDto());
                model.addAttribute("transactionInputs", new TransactionDto());

            } else {
                model.addAttribute("accountSearchInputs", new AccountSearchInputDto(accountIdLong));
                var foundAccount = _accountService.loadAccount(accountIdLong);
                var transactionDto = new TransactionDto();
                transactionDto.setAccountCustomerName(foundAccount.getCustomerName());
                transactionDto.setAccountBalance(foundAccount.getBalance());
                transactionDto.setAccountCurrency(foundAccount.getCurrency());
                transactionDto.setAccountId(foundAccount.getId());
                transactionDto.setCurrency(foundAccount.getCurrency());
                model.addAttribute("transactionInputs", transactionDto);
            }

            return "transaction";
        } catch (BusinessException ex) {
            return "redirect:/transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/transaction/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/account/index?error=Invalid+input+parameters";
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/transaction/index";
        }

        return "redirect:/transaction/index?account_id=" + accountId;
    }

    @PostMapping("/addTransaction")
    public String addSubmit(@ModelAttribute TransactionDto transactionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/transaction/index?error=Invalid+input+parameters";
        }

        try {
            // TODO: Get From Current User
            transactionDto.setUserId(1L);

            _transactionService.doTransaction(transactionDto);

            return "redirect:/transaction/index";
        } catch (BusinessException ex) {
            return "redirect:/transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/transaction/index?error=unhandled+error+occurred";
        }
    }
}
