package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/branch_transaction")
@Layout(title = "Branch Transactions", value = "layouts/default")
public class BranchTransactionController {
    private final TransactionService _transactionService;

    public BranchTransactionController(TransactionService transactionService) {
        _transactionService = transactionService;
    }

    @GetMapping("/index")
    public String loadForm(Model model) {
        try {
            var transactionDtoList = _transactionService.loadLastBranchTransactions();
            model.addAttribute("transactionDtoList", transactionDtoList);

            return "branch_transaction";
        } catch (BusinessException ex) {
            return "redirect:/branch_transaction/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/branch_transaction/index?error=unhandled+error+occurred";
        }
    }
}
