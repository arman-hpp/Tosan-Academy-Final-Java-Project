package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerDefaultErrors;
import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.web.RequestParamsBuilder;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.AccountSearchInputDto;
import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.loan.dtos.InstallmentDto;
import com.tosan.loan.dtos.LoanSearchInputDto;
import com.tosan.loan.dtos.PayInstallmentInputDto;
import com.tosan.loan.services.InstallmentService;
import com.tosan.loan.services.PayInstallmentService;
import com.tosan.model.Currencies;
import com.tosan.utils.ConvertorUtils;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
@RequestMapping("/pay_installment")
@Layout(title = "Pay Installments", value = "layouts/default")
@RolesAllowed("ROLE_USER")
public class PayInstallmentController {
    private final InstallmentService _installmentService;
    private final PayInstallmentService _payInstallmentService;
    private final AccountService _accountService;
    private final AuthenticationService _authenticationService;

    public PayInstallmentController(InstallmentService installmentService,
                                    PayInstallmentService payInstallmentService,
                                    AccountService accountService,
                                    AuthenticationService authenticationService) {
        _installmentService = installmentService;
        _payInstallmentService = payInstallmentService;
        _accountService = accountService;
        _authenticationService = authenticationService;
    }

    @GetMapping({"/","/index"})
    public String loadForm(
            @RequestParam(name = "loan_id", required = false) String loanId,
            @RequestParam(name = "installment_count", required = false) String installmentCount,
            @RequestParam(name = "account_id", required = false) String accountId,
            Model model) {

        var loanIdLong = ConvertorUtils.tryParseLong(loanId, null);
        var installmentCountLong = ConvertorUtils.tryParseInt(installmentCount, null);
        var accountIdLong = ConvertorUtils.tryParseLong(accountId, null);

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
                    var accountDto = _accountService.loadCustomerAccount(accountIdLong);
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

            return "views/user/pay_installment";
        } catch (Exception ex) {
            if(loanIdLong != null) {
                return new RequestParamsBuilder("redirect:/pay_installment/index")
                        .Add("loan_id", loanIdLong)
                        .Add("error", ControllerErrorParser.getError(ex))
                        .toString();
            }

            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchLoan")
    public String searchLoanSubmit(@ModelAttribute LoanSearchInputDto loanSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        return "redirect:/pay_installment/index?loan_id=" + loanSearchInputDto.getLoanId();
    }

    @PostMapping("/calculateInstallments")
    public String calculateInstallments(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            return new RequestParamsBuilder("redirect:/pay_installment/index")
                    .Add("loan_id", payInstallmentInputDto.getLoanId())
                    .Add("installment_count", payInstallmentInputDto.getInstallmentCount())
                    .toString();

        }catch (Exception ex) {
            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(ex);
        }
    }


    @PostMapping("/searchAccount")
    public String searchAccountSubmit(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var installmentCount = payInstallmentInputDto.getInstallmentCount();
        if (installmentCount == null) {
            return "redirect:/pay_installment/index?loan_id=" + payInstallmentInputDto.getLoanId();
        }

        var accountId = payInstallmentInputDto.getAccountId();
        if (accountId == null) {
            return new RequestParamsBuilder("redirect:/pay_installment/index")
                    .Add("loan_id", payInstallmentInputDto.getLoanId())
                    .Add("installment_count", installmentCount)
                    .toString();
        }

        return new RequestParamsBuilder("redirect:/pay_installment/index")
                .Add("loan_id", payInstallmentInputDto.getLoanId())
                .Add("installment_count", installmentCount)
                .Add("account_id", accountId)
                .toString();
    }

    @PostMapping("/addTransaction")
    public String addSubmit(@ModelAttribute PayInstallmentInputDto payInstallmentInputDto,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if (currentUserId == null) {
                return  "redirect:/pay_installment/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
            }

            _payInstallmentService.payInstallments(payInstallmentInputDto.getLoanId(),
                    payInstallmentInputDto.getAccountId(), currentUserId,
                    payInstallmentInputDto.getInstallmentCount());

            return "redirect:/pay_installment/index?loan_id=" + payInstallmentInputDto.getLoanId();
        } catch (Exception ex) {
            model.addAttribute("loanSearchInputDto",
                    new LoanSearchInputDto(payInstallmentInputDto.getLoanId()));
            var installments = _installmentService
                    .loadInstallments(payInstallmentInputDto.getLoanId());
            model.addAttribute("installmentDtoList", installments);
            ControllerErrorParser.setError(bindingResult, ex);

            return "views/user/pay_installment";
        }
    }
}