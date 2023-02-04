package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.*;

import org.springframework.stereotype.Controller;
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


}
