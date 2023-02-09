package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.BindingResultHelper;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.AccountTypes;
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
    private final AuthenticationService _authenticationService;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 AuthenticationService authenticationService) {
        _transactionService = transactionService;
        _accountService = accountService;
        _authenticationService = authenticationService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "account_id", required = false) String accountId,
                           Model model) {
        try {
            Long accountIdLong = null;
            if (accountId != null) {
                accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
                if (accountIdLong <= 0) {
                    return "redirect:/transaction/index?error=Invalid+input+parameters";
                }
            }

            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if (currentUserId == null) {
                return BindingResultHelper.getIllegalAccessError("redirect:/transaction/index");
            }

            var transactionDtoList = _transactionService.loadUserTransactions(currentUserId);
            model.addAttribute("transactionDtoList", transactionDtoList);

            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("transactionDto", new TransactionDto());

            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));
                var foundAccount = _accountService.loadAccount(accountIdLong);

                if(foundAccount.getAccountType() == AccountTypes.BankAccount) {
                    if(!_authenticationService.isUserAdmin()) {
                        return "redirect:/transaction/index?error=You+are+not+authorized+to+perform+operations+on+the+bank+account";
                    }
                }

                if(_authenticationService.isUserAdmin()) {
                    if(foundAccount.getAccountType() == AccountTypes.CustomerAccount) {
                        return "redirect:/transaction/index?error=You+are+not+authorized+to+perform+operations+on+the+customer+account";
                    }
                } else
                {
                    if(foundAccount.getAccountType() == AccountTypes.BankAccount) {
                        return "redirect:/transaction/index?error=You+are+not+authorized+to+perform+operations+on+the+bank+account";
                    }
                }

                var transactionDto = new TransactionDto();
                transactionDto.setAccountCustomerName(foundAccount.getCustomerName());
                transactionDto.setAccountBalance(foundAccount.getBalance());
                transactionDto.setAccountCurrency(foundAccount.getCurrency());
                transactionDto.setAccountId(foundAccount.getId());
                transactionDto.setCurrency(foundAccount.getCurrency());
                model.addAttribute("transactionDto", transactionDto);
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
            return "redirect:/transaction/index?error=Invalid+input+parameters";
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
            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if (currentUserId == null) {
                return BindingResultHelper.getIllegalAccessError("redirect:/transaction/index");
            }

            transactionDto.setUserId(currentUserId);

            _transactionService.doTransaction(transactionDto);

            return "redirect:/transaction/index";
        } catch (BusinessException ex) {
            return "redirect:/transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/transaction/index?error=unhandled+error+occurred";
        }
    }
}