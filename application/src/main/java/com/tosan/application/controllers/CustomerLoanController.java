package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.CustomerSearchInputDto;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.services.LoanService;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/customer_loan")
@Layout(title = "Customer Loans", value = "layouts/default")
public class CustomerLoanController {
    private final LoanService _loanService;

    public CustomerLoanController(LoanService loanService) {
        _loanService = loanService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(name = "customer_id", required = false) String customerId,
                           Model model) {
        try {
            Long customerIdLong = null;
            if (customerId != null) {
                customerIdLong = ConvertorUtils.tryParseLong(customerId, -1L);
                if (customerIdLong <= 0) {
                    return "redirect:/customer_loan/index?error=Invalid+input+parameters";
                }
            }

            if (customerIdLong == null) {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());
                model.addAttribute("loanDtoList", new ArrayList<LoanDto>());
            } else {
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto(customerIdLong));
                var loanDtoList = _loanService.loadLoansByCustomerId(customerIdLong);
                model.addAttribute("loanDtoList", loanDtoList);
            }

            return "customer_loan";
        } catch (BusinessException ex) {
            return "redirect:/customer_loan/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer_loan/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/searchCustomer")
    public String searchCustomerSubmit(@ModelAttribute CustomerSearchInputDto customerSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/customer_loan/index?error=Invalid+input+parameters";
        }

        var customerId = customerSearchInputDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/customer_loan/index";
        }

        return "redirect:/customer_loan/index?customer_id=" + customerId;
    }

}
