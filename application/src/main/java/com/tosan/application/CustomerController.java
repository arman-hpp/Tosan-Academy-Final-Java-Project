package com.tosan.application;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.CustomerDto;
import com.tosan.core_banking.dtos.CustomerInputDto;
import com.tosan.core_banking.exceptions.BankException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
@Layout(title = "Customers", value = "layouts/default")
public class CustomerController {
    @GetMapping("/customer")
    public String customerForm(Model model) {
        model.addAttribute("customerInputDto", new CustomerInputDto());

        List<CustomerDto> users = new ArrayList<>();
        users.add(new CustomerDto(1L,"Arman", "Arian", "Babol"));
        users.add(new CustomerDto(2L,"Arash", "HAA", "Babol"));
        model.addAttribute("customerList", users);

        return "customer";
    }

    @PostMapping("/customer")
    public String customerSubmit(@ModelAttribute CustomerInputDto customerInputDto, Model model) {
        try {
            return "redirect:/customer";
        }
        catch (BankException ex) {
            return "redirect:/customer?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/customer?error=unhandled+error+occurred";
        }
    }
}
