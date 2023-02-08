package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.BindingResultHelper;
import com.tosan.application.extensions.springframework.RequestParamsBuilder;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.InstallmentDto;
import com.tosan.loan.dtos.LoanSearchInputDto;
import com.tosan.loan.dtos.PayInstallmentInputDto;
import com.tosan.loan.services.InstallmentService;
import com.tosan.loan.services.PayInstallmentService;
import com.tosan.model.Currencies;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
@RequestMapping("/loan_installment")
@Layout(title = "Loan Installments", value = "layouts/default")
public class LoanInstallmentController {
    private final InstallmentService _installmentService;
    private final PayInstallmentService _payInstallmentService;
    private final AccountService _accountService;
    private final AuthenticationService _authenticationService;

    public LoanInstallmentController(InstallmentService installmentService,
                                     PayInstallmentService payInstallmentService,
                                     AccountService accountService,
                                     AuthenticationService authenticationService) {
        _installmentService = installmentService;
        _payInstallmentService = payInstallmentService;
        _accountService = accountService;
        _authenticationService = authenticationService;
    }

    @GetMapping("/index")
    public String loadForm(
            @RequestParam(name = "loan_id", required = false) String loanId,
            @RequestParam(name = "installment_count", required = false) String installmentCount,
            @RequestParam(name = "account_id", required = false) String accountId,
            Model model) {

        Long loanIdLong = null;
        if (loanId != null) {
            loanIdLong = ConvertorUtils.tryParseLong(loanId, -1L);
            if (loanIdLong <= 0) {
                return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
            }
        }

        Integer installmentCountLong = null;
        if (installmentCount != null) {
            installmentCountLong = ConvertorUtils.tryParseInt(installmentCount, -1);
            if (installmentCountLong <= 0) {
                return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
            }
        }

        Long accountIdLong = null;
        if (accountId != null) {
            accountIdLong = ConvertorUtils.tryParseLong(accountId, -1L);
            if (accountIdLong <= 0) {
                return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
            }
        }

        try {
            if (loanIdLong == null) {
                model.addAttribute("accountSearchInputDto", new AccountSearchInputDto());
                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto());
                model.addAttribute("installmentDtoList", new ArrayList<InstallmentDto>());
                model.addAttribute("payInstallmentInputDto", new PayInstallmentInputDto());
            } else {
                BigDecimal amount = null;
                String accountCustomerName = null;
                Currencies accountCurrency = null;
                BigDecimal accountBalance = null;

                if (accountIdLong != null) {
                    var accountDto = _accountService.loadAccount(accountIdLong);
                    accountCustomerName = accountDto.getCustomerName();
                    accountCurrency = accountDto.getCurrency();
                    accountBalance = accountDto.getBalance();

                    model.addAttribute("accountSearchInputDto", new AccountSearchInputDto(accountIdLong));
                }

                if (installmentCountLong != null) {
                    amount = _payInstallmentService.sumNonPaidInstallment(loanIdLong, installmentCountLong);
                }

                model.addAttribute("payInstallmentInputDto",
                        new PayInstallmentInputDto(loanIdLong, accountIdLong, installmentCountLong, amount,
                                accountCustomerName, accountBalance, accountCurrency));

                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto(loanIdLong));

                var installments = _installmentService.loadInstallments(loanIdLong);
                model.addAttribute("installmentDtoList", installments);
            }

            model.addAttribute("transactionDto", new TransactionDto());

            return "loan_installment";
        } catch (Exception ex) {
            if(loanIdLong != null) {
                var requestParams = RequestParamsBuilder
                        .build("loan_id", loanIdLong, "error", ex.getMessage());

                return "redirect:/loan_installment/index" + requestParams;
            }

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

    @PostMapping("/calculateInstallments")
    public String calculateInstallments(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_installment/index?error=Invalid+input+parameters";
        }

        try {
            var requestParams = RequestParamsBuilder.build(
                    "loan_id", payInstallmentInputDto.getLoanId(),
                    "installment_count", payInstallmentInputDto.getInstallmentCount());

            return "redirect:/loan_installment/index" + requestParams;
        } catch (BusinessException ex) {
            return "redirect:/loan_installment/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan_installment/index?error=unhandled+error+occurred";
        }
    }


    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
        }

        var loanId = payInstallmentInputDto.getLoanId();
        if (loanId == null) {
            return BindingResultHelper.getInputValidationError("redirect:/loan_installment/index");
        }

        var installmentCount = payInstallmentInputDto.getInstallmentCount();
        if (installmentCount == null) {
            var requestParams = RequestParamsBuilder.build("loan_id", loanId);
            return "redirect:/loan_installment/index" + requestParams;
        }

        var accountId = payInstallmentInputDto.getAccountId();
        if (accountId == null) {
            var requestParams = RequestParamsBuilder.build(
                    "loan_id", loanId,
                    "installment_count", installmentCount);

            return "redirect:/loan_installment/index" + requestParams;
        }

        var requestParams = RequestParamsBuilder.build(
                "loan_id", loanId,
                "installment_count", installmentCount,
                "account_id", accountId);

        return "redirect:/loan_installment/index" + requestParams;
    }

    @PostMapping("/addTransaction")
    public String addSubmit(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_installment/index?error=Invalid+input+parameters";
        }

        try {
            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if (currentUserId == null) {
                return BindingResultHelper.getIllegalAccessError("redirect:/loan_installment/index");
            }

            _payInstallmentService.payInstallments(payInstallmentInputDto.getLoanId(),
                    payInstallmentInputDto.getAccountId(), currentUserId,
                    payInstallmentInputDto.getInstallmentCount());

            return "redirect:/loan_installment/index?loan_id=" + payInstallmentInputDto.getLoanId();
        } catch (Exception ex) {
            model.addAttribute("loanSearchInputDto",
                    new LoanSearchInputDto(payInstallmentInputDto.getLoanId()));

            var installments = _installmentService
                    .loadInstallments(payInstallmentInputDto.getLoanId());
            model.addAttribute("installmentDtoList", installments);

            BindingResultHelper.addGlobalError(bindingResult, ex);

            return "loan_installment";
        }
    }
}