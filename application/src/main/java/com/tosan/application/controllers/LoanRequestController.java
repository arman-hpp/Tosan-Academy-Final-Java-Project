package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.ControllerErrorParser;
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
public class LoanRequestController {
    private final LoanService _loanService;
    private final AccountService _accountService;

    public LoanRequestController(LoanService loanService, AccountService accountService) {
        _loanService = loanService;
        _accountService = accountService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "account_id", required = false) String accountId, Model model) {
        var accountIdLong = ConvertorUtils.tryParseLong(accountId, null);
        try {
            var loanDtoList = _loanService.loadLoans();
            model.addAttribute("loanDtoList", loanDtoList);

            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("loanDto", new LoanDto());
            } else {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));
                var foundAccount = _accountService.loadCustomerAccount(accountIdLong);
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
           return "redirect:/loan_request/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getInvalidArgumentError();
        }

        try {
            var foundLoan = _loanService.loadLoan(idLong);

            model.addAttribute("loanDto", foundLoan);

            var loanDtoList = _loanService.loadLoans();
            model.addAttribute("loanDtoList", loanDtoList);

            model.addAttribute("accountSearchInputDto",
                    new AccountSearchInputDto(foundLoan.getAccountId()));

            return "loan_request";
        } catch (Exception ex) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute AccountSearchInputDto accountSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        return "redirect:/loan_request/index?account_id=" + accountSearchInputDto.getAccountId();
    }

    @PostMapping("/addLoanRequest")
    public String addSubmit(@ModelAttribute LoanDto loanDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            _loanService.addOrEditLoan(loanDto);

            return "redirect:/loan_request/index";
        } catch (Exception ex) {
            var loanDtoList = _loanService.loadLoans();
            model.addAttribute("loanDtoList", loanDtoList);
            model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(loanDto.getAccountId()));
            ControllerErrorParser.setError(bindingResult, ex);

            return "loan_request";
        }
    }

    @PostMapping("/deleteLoanRequest/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
           return "redirect:/loan_request/index?error=" + ControllerErrorParser.getInvalidArgumentError();
        }

        try {
            _loanService.removeLoan(idLong);

            return "redirect:/loan_request/index";
        } catch (Exception ex) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/editLoanRequest/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/loan_request/index?error=" + ControllerErrorParser.getInvalidArgumentError();
        }

        return "redirect:/loan_request/index/" + idLong;
    }
}
