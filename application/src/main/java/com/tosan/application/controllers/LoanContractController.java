package com.tosan.application.controllers;

import com.tosan.application.extensions.errors.ControllerDefaultErrors;
import com.tosan.application.extensions.errors.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.loan.dtos.InstallmentDto;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.dtos.LoanSearchInputDto;
import com.tosan.loan.services.DepositLoanService;
import com.tosan.loan.services.InstallmentService;
import com.tosan.loan.services.LoanService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/loan_contract")
@Layout(title = "Loan Contracts", value = "layouts/default")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class LoanContractController {
    private final LoanService _loanService;
    private final InstallmentService _installmentService;
    private final DepositLoanService _depositLoanService;
    private final AuthenticationService _authenticationService;

    public LoanContractController(LoanService loanService,
                                  InstallmentService installmentService,
                                  DepositLoanService depositLoanService,
                                  AuthenticationService authenticationService) {
        _loanService = loanService;
        _installmentService = installmentService;
        _depositLoanService = depositLoanService;
        _authenticationService = authenticationService;
    }

    @GetMapping({"/","/index"})
    public String loadForm(@RequestParam(name = "loan_id", required = false) String loanId, Model model) {
        var loanIdLong = ConvertorUtils.tryParseLong(loanId, null);

        try {
            if (loanIdLong == null) {
                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto());
                model.addAttribute("loanDto", new LoanDto());
                model.addAttribute("installmentDtoList", new ArrayList<InstallmentDto>());
            } else {
                model.addAttribute("loanSearchInputDto", new LoanSearchInputDto(loanIdLong));
                var loanDto = _loanService.loadLoan(loanIdLong);
                model.addAttribute("loanDto", loanDto);
                var installments = _installmentService.loadInstallments(loanIdLong);
                model.addAttribute("installmentDtoList", installments);
            }

            return "views/user/loan_contract";
        } catch (Exception ex) {
            return "redirect:/loan_contract/index?error=" + ControllerErrorParser.getError(ex);
        }
    }

    @PostMapping("/searchLoan")
    public String searchLoanSubmit(@ModelAttribute LoanSearchInputDto loanSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_contract/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var loanId = loanSearchInputDto.getLoanId();
        if (loanId == null) {
            return "redirect:/loan_contract/index";
        }

        return "redirect:/loan_contract/index?loan_id=" + loanId;
    }

    @PostMapping("/depositLoan")
    public String depositSubmit(@ModelAttribute LoanDto loanDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_contract/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        try {
            var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
            if(currentUserId == null){
                return "redirect:/loan_contract/index?error=" + ControllerErrorParser.getError(ControllerDefaultErrors.IllegalAccess);
            }

            _depositLoanService.depositLoan(currentUserId, loanDto.getId());

            return "redirect:/loan_contract/index?loan_id=" + loanDto.getId();
        } catch (Exception ex) {
            var installments = _installmentService.loadInstallments(loanDto.getId());
            model.addAttribute("installmentDtoList", installments);
            model.addAttribute("loanSearchInputDto", new LoanSearchInputDto(loanDto.getId()));

            ControllerErrorParser.setError(bindingResult, ex);

            return "views/user/loan_contract";
        }
    }
}
