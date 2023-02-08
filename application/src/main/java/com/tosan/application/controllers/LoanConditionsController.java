package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanConditionsDto;
import com.tosan.loan.services.LoanConditionsService;
import com.tosan.model.Currencies;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loan_condition")
@Layout(title = "Loan Conditions", value = "layouts/default")
public class LoanConditionsController {
    private final LoanConditionsService _loanConditionsService;

    public LoanConditionsController(LoanConditionsService loanConditionsService) {
        _loanConditionsService = loanConditionsService;
    }

    @GetMapping("/index")
    public String loadForm(
            @RequestParam(name = "currency", required = false) String currency,
            Model model) {
        try {
            Currencies currencyEnum = null;
            if (currency != null) {
                currencyEnum = Currencies.valueOf(currency);
            }

            if (currencyEnum == null) {
                model.addAttribute("loanConditionsDto", new LoanConditionsDto());
            } else {
                var loanConditionsDto = _loanConditionsService.loadLoanCondition(currencyEnum);
                model.addAttribute("loanConditionsDto", loanConditionsDto);
            }

            return "loan_condition";
        } catch (BusinessException ex) {
            return "redirect:/loan_condition/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan_condition/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchCurrency")
    public String searchCurrencySubmit(@ModelAttribute LoanConditionsDto loanConditionsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_condition/index?error=Invalid+input+parameters";
        }

        var currency =loanConditionsDto.getCurrency();
        if (currency == null) {
            return "redirect:/loan_condition/index";
        }

        return "redirect:/loan_condition/index?currency=" + currency;
    }

    @PostMapping("/editLoanConditions")
    public String editSubmit(@ModelAttribute LoanConditionsDto loanConditionsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_condition/index?error=Invalid+input+parameters";
        }

        try {
            _loanConditionsService.editLoanConditions(loanConditionsDto);

            return "redirect:/loan_condition/index?currency=" + loanConditionsDto.getCurrency().toString();
        } catch (BusinessException ex) {
            return "redirect:/loan_condition/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/loan_condition/index?error=unhandled+error+occurred";
        }
    }
}
