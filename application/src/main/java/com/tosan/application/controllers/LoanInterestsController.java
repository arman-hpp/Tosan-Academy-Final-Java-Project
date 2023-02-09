package com.tosan.application.controllers;

import com.tosan.application.extensions.springframework.ControllerErrorParser;
import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.loan.dtos.LoanInterestSearchDto;
import com.tosan.loan.services.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/loan_interest")
@Layout(title = "Loan Interests", value = "layouts/default")
public class LoanInterestsController {
    private final LoanService _loanService;

    public LoanInterestsController(LoanService loanService) {
        _loanService = loanService;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        var fromDateTime = LocalDateTime.now().minusYears(1).withHour(0).withMinute(0).withSecond(1);
        var toDateTime = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        model.addAttribute("loanInterestSearchDto", new LoanInterestSearchDto(fromDateTime, toDateTime));

        return "loan_interest";
    }

    @PostMapping("/calcInterests")
    public String calcInterestsSubmit(@ModelAttribute LoanInterestSearchDto loanInterestSearchDto,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/loan_interest/index?error=" + ControllerErrorParser.getError(bindingResult);
        }

        var future = _loanService
                .loadLoanSumInterests(loanInterestSearchDto.fromDate, loanInterestSearchDto.toDate);

        try {
            var loanInterestStatisticsDtoList = future.get();
            model.addAttribute("loanInterestStatisticsDtoList", loanInterestStatisticsDtoList);
        } catch(ExecutionException | InterruptedException ex)  {
            ControllerErrorParser.setError(bindingResult, ex);
        }

        return "loan_interest";
    }

}
