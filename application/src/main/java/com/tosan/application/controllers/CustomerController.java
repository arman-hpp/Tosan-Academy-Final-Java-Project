package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.services.CustomerService;
import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.exceptions.BankException;

import com.tosan.utils.Convertors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/customer")
@Layout(title = "Customers", value = "layouts/default")
public class CustomerController {
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }

    @GetMapping("/index")
    public String customerForm(@RequestParam(required = false) String id, Model model) {
        try {
            if (id != null) {
                var idLong = Convertors.tryParseLong(id, -1L);
                if(idLong <= 0) {
                    return "redirect:/customer/index?error=Invalid+input+parameters";
                }

                var foundCustomer = _customerService.loadCustomer(idLong);
                model.addAttribute("customerInputs", new CustomerDto());
                model.addAttribute("customerOutputs", foundCustomer);
                model.addAttribute("customerSearchInputs", new CustomerSearchInputDto(idLong));
            } else {
                var customers = _customerService.loadCustomers();
                model.addAttribute("customerInputs", new CustomerDto());
                model.addAttribute("customerOutputs", customers);
                model.addAttribute("customerSearchInputs", new CustomerSearchInputDto());
            }

            return "customer";
        } catch (BankException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/index/{id}")
    public String customerFormById(@PathVariable String id, Model model) {
        var idLong = Convertors.tryParseLong(id, -1L);
        if(idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        try {
            var foundCustomer = _customerService.loadCustomer(idLong);
            model.addAttribute("customerInputs", foundCustomer);

            var customers = _customerService.loadCustomers();
            model.addAttribute("customerOutputs", customers);

            model.addAttribute("customerSearchInputs", new CustomerSearchInputDto());

            return "customer";
        } catch (BankException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/addCustomer")
    public String addCustomerSubmit(@ModelAttribute CustomerDto customerDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

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
    public String deleteSubmit(@PathVariable String id) {
        var idLong = Convertors.tryParseLong(id, -1L);
        if(idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        try {
            _customerService.removeCustomer(idLong);

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
    public String editSubmit(@PathVariable String id) {
        var idLong = Convertors.tryParseLong(id, -1L);
        if(idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        return "redirect:/customer/index/" + idLong;
    }

    @PostMapping("/searchCustomer")
    public String searchSubmit(@ModelAttribute CustomerSearchInputDto searchCustomerInputsDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        var id = searchCustomerInputsDto.getId();
        if(id == null){
            return "redirect:/customer/index";
        }

        return "redirect:/customer/index?id=" + id;
    }
}
