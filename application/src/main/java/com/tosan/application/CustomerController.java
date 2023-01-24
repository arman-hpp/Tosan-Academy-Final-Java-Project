package com.tosan.application;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.exceptions.BankException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customer")
@Layout(title = "Customers", value = "layouts/default")
public class CustomerController {
    @GetMapping("/index")
    public String customerForm(@RequestParam(required = false) Long id, @RequestParam(required = false) Long customerNo, Model model) {
        if(id != null) {
            model.addAttribute("customerInputDto", new CustomerInputDto("Arash", "HAA", "Babol"));
        } else {
            model.addAttribute("customerInputDto", new CustomerInputDto());
        }

        if(customerNo != null) {
            List<CustomerOutputDto> users = new ArrayList<>();
            users.add(new CustomerOutputDto(2L,1L, "Arash", "HAA", "Babol"));
            model.addAttribute("customerList", users);
            model.addAttribute("searchCustomerInputsDto", new SearchCustomerInputDto(customerNo));
        } else {
            List<CustomerOutputDto> users = new ArrayList<>();
            users.add(new CustomerOutputDto(1L,0L,"Arman", "Arian", "Babol"));
            users.add(new CustomerOutputDto(2L, 1L,"Arash", "HAA", "Babol"));
            model.addAttribute("customerList", users);
            model.addAttribute("searchCustomerInputsDto", new SearchCustomerInputDto());
        }

        return "customer";
    }

    @PostMapping("/addCustomer")
    public String addCustomerSubmit(@ModelAttribute CustomerInputDto customerInputDto, Model model) {
        try {
            return "redirect:/customer/index";
        }
        catch (BankException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/deleteCustomer/{id}")
    public String deleteSubmit(@PathVariable Long id) {
        try {
            return "redirect:/customer/index";
        }
        catch (BankException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        }
        catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/editCustomer/{id}")
    public String editSubmit(@PathVariable Long id) {
        return "redirect:/customer/index?id=" + id.toString();
    }

    @PostMapping("/searchCustomer")
    public String searchSubmit(@ModelAttribute SearchCustomerInputDto searchCustomerInputsDto, Model model) {
        return "redirect:/customer/index?customerNo=" + searchCustomerInputsDto.getCustomerNo().toString();
    }
}
