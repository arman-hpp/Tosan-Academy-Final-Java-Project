package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.services.LoanService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loan")
@Layout(title = "Loans", value = "layouts/default")
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
                    return "redirect:/loan/index?error=Invalid+input+parameters";
                }
            }

            var loans = _loanService.loadLoans();
            model.addAttribute("loanOutputs", loans);

            if (accountIdLong == null) {
                model.addAttribute("accountSearchInputs", new AccountSearchInputDto());
                model.addAttribute("loanInputs", new LoanDto());

            } else {
                model.addAttribute("accountSearchInputs", new AccountSearchInputDto(accountIdLong));

                var foundAccount = _accountService.loadAccount(accountIdLong);
                var loanDto = new LoanDto();
                loanDto.setAccountCustomerName(foundAccount.getCustomerName());
                loanDto.setAccountBalance(foundAccount.getBalance());
                loanDto.setAccountCurrency(foundAccount.getCurrency());
                loanDto.setAccountId(foundAccount.getId());
                loanDto.setCurrency(foundAccount.getCurrency());
                loanDto.setCustomerId(foundAccount.getCustomerId());

                model.addAttribute("loanInputs", loanDto);
            }

            return "loan";
        } catch (BusinessException ex) {
            return "redirect:/loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/loan/index?error=Invalid+input+parameters";
        }

        try {
            var foundLoan = _loanService.loadLoan(idLong);
            model.addAttribute("loanInputs", foundLoan);

            var loans = _loanService.loadLoans();
            model.addAttribute("loanOutputs", loans);

            model.addAttribute("accountSearchInputs",
                    new AccountSearchInputDto(foundLoan.getAccountId()));

            return "loan";
        } catch (BusinessException ex) {
            return "redirect:/loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan/index?error=unhandled+error+occurred";
        }
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

    @PostMapping("/addLoan")
    public String addSubmit(@ModelAttribute LoanDto loanDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan/index?error=Invalid+input+parameters";
        }

        try {
            _loanService.addOrEditLoan(loanDto);

            return "redirect:/loan/index";
        } catch (BusinessException ex) {
            return "redirect:/loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/deleteLoan/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/loan/index?error=Invalid+input+parameters";
        }

        try {
            _loanService.removeLoan(idLong);

            return "redirect:/loan/index";
        } catch (BusinessException ex) {
            return "redirect:/loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/editLoan/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/loan/index?error=Invalid+input+parameters";
        }

        return "redirect:/loan/index/" + idLong;
    }
}
