package com.tosan.application;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.CustomerService;
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
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }

    @GetMapping("/index")
    public String customerForm(@RequestParam(required = false) Long id, Model model) {
        if(id != null) {
            var foundCustomer = _customerService.loadCustomer(id);
            model.addAttribute("customerInputs", foundCustomer);
            model.addAttribute("customerOutputs", foundCustomer);
            model.addAttribute("customerSearchInputs", new SearchCustomerInputDto(id));
        } else {
            var customers = _customerService.loadCustomers();
            model.addAttribute("customerInputs", new CustomerDto());
            model.addAttribute("customerOutputs", customers);
            model.addAttribute("customerSearchInputs", new SearchCustomerInputDto());
        }

        return "customer";
    }

    @PostMapping("/addCustomer")
    public String addCustomerSubmit(@ModelAttribute CustomerDto customerDto, Model model) {
        try {
            _customerService.addOrEditCustomer(customerDto);

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
            _customerService.removeCustomer(id);

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
        return "redirect:/customer/index?id=" + searchCustomerInputsDto.getId().toString();
    }
}
