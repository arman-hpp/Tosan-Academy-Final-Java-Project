package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.BindingResultHelper;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.services.LoanService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loan_request")
@Layout(title = "Loan Requests", value = "layouts/default")
public class LoanController {
    private final LoanService _loanService;
    private final AccountService _accountService;

    public LoanController(LoanService loanService, AccountService accountService) {
        _loanService = loanService;
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
                    return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
                }
            }

            var loans = _loanService.loadLoans();
            model.addAttribute("loanOutputs", loans);

            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("loanDto", new LoanDto());

            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));

                var foundAccount = _accountService.loadAccount(accountIdLong);
                var loanDto = new LoanDto();
                loanDto.setAccountCustomerName(foundAccount.getCustomerName());
                loanDto.setAccountBalance(foundAccount.getBalance());
                loanDto.setAccountCurrency(foundAccount.getCurrency());
                loanDto.setAccountId(foundAccount.getId());
                loanDto.setCurrency(foundAccount.getCurrency());
                loanDto.setCustomerId(foundAccount.getCustomerId());

                model.addAttribute("loanDto", loanDto);
            }

            return "loan_request";
        } catch (Exception ex) {
           return BindingResultHelper.getGlobalError("redirect:/loan_request/index", ex);
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
        }

        try {
            var foundLoan = _loanService.loadLoan(idLong);

            model.addAttribute("loanDto", foundLoan);

            var loans = _loanService.loadLoans();
            model.addAttribute("loanOutputs", loans);

            model.addAttribute("accountSearchInputDto",
                    new AccountSearchInputDto(foundLoan.getAccountId()));

            return "loan_request";
        } catch (Exception ex) {
            return BindingResultHelper.getGlobalError("redirect:/loan_request/index", ex);
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
        }

        var accountId = accountSearchInputDto.getAccountId();
        if (accountId == null) {
            return "redirect:/loan_request/index";
        }

        return "redirect:/loan_request/index?account_id=" + accountId;
    }

    @PostMapping("/addLoanRequest")
    public String addSubmit(@ModelAttribute LoanDto loanDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
        }

        try {
            _loanService.addOrEditLoan(loanDto);

            return "redirect:/loan_request/index";
        } catch (Exception ex) {
            var loans = _loanService.loadLoans();
            model.addAttribute("loanOutputs", loans);
            model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(loanDto.getAccountId()));
            BindingResultHelper.addGlobalError(bindingResult, ex);

            return "loan_request";
        }
    }

    @PostMapping("/deleteLoanRequest/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
           return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
        }

        try {
            _loanService.removeLoan(idLong);

            return "redirect:/loan_request/index";
        } catch (Exception ex) {
            return BindingResultHelper.getGlobalError("redirect:/loan_request/index", ex);
        }
    }

    @PostMapping("/editLoanRequest/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
           return BindingResultHelper.getInputValidationError("redirect:/loan_request/index");
        }

        return "redirect:/loan_request/index/" + idLong;
    }
}
