package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.BindingResultHelper;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.InstallmentDto;
import com.tosan.loan.dtos.LoanSearchInputDto;
import com.tosan.loan.dtos.PayInstallmentInputDto;
import com.tosan.loan.services.InstallmentService;
import com.tosan.loan.services.LoanService;
import com.tosan.loan.services.PayInstallmentService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/loan_installment")
@Layout(title = "Loan Installments", value = "layouts/default")
public class LoanInstallmentController {
    private final LoanService _loanService;
    private final InstallmentService _installmentService;
    private final PayInstallmentService _payInstallmentService;
    private final AccountService _accountService;
    public LoanInstallmentController(LoanService loanService,
                                     InstallmentService installmentService,
                                     PayInstallmentService payInstallmentService, AccountService accountService) {
        _loanService = loanService;
        _installmentService = installmentService;
        _payInstallmentService = payInstallmentService;
        _accountService = accountService;
    }

    @GetMapping("/index")
    public String loadForm(
            @RequestParam(name = "loan_id", required = false) String loanId,
            Model model) {
        try {
            Long loanIdLong = null;
            if (loanId != null) {
                loanIdLong = ConvertorUtils.tryParseLong(loanId, -1L);
                if (loanIdLong <= 0) {
                    return BindingResultHelper.getInputValidationError("redirect:/loan_contract/index");
                }
            }

            if (loanIdLong == null) {
                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto());
                model.addAttribute("installmentDtoList", new ArrayList<InstallmentDto>());
                model.addAttribute("payInstallmentInputDto", new PayInstallmentInputDto());
            } else {
                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto(loanIdLong));
                model.addAttribute("payInstallmentInputDto", new PayInstallmentInputDto(loanIdLong, null, null));
                var installments = _installmentService.loadInstallments(loanIdLong);
                model.addAttribute("installmentDtoList", installments);
            }

            model.addAttribute("transactionDto", new TransactionDto());

            return "loan_installment";
        } catch (Exception ex) {
            return BindingResultHelper.getGlobalError("redirect:/loan_installment/index", ex);
        }
    }

    @PostMapping("/searchLoan")
    public String searchLoanSubmit(@ModelAttribute LoanSearchInputDto loanSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
        }

        var loanId = loanSearchInputDto.getLoanId();
        if (loanId == null) {
            return "redirect:/loan_installment/index";
        }

        return "redirect:/loan_installment/index?loan_id=" + loanId;
    }
    @PostMapping("/addTransaction")
    public String addSubmit(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_installment/index?error=Invalid+input+parameters";
        }

        try {
            return "redirect:/transaction/index?account_id=" + payInstallmentInputDto.getAccountId();
        } catch (BusinessException ex) {
            return "redirect:/loan_installment/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan_installment/index?error=unhandled+error+occurred";
        }
    }
}
