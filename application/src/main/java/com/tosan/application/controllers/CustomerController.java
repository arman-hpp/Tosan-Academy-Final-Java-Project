package com.tosan.application.controllers;

import com.tosan.application.extensions.thymeleaf.Layout;
import com.tosan.core_banking.dtos.CustomerDto;
import com.tosan.core_banking.dtos.CustomerSearchInputDto;
import com.tosan.core_banking.services.CustomerService;
import com.tosan.exceptions.BusinessException;
import com.tosan.utils.ConvertorUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
@Layout(title = "Customers", value = "layouts/default")
public class CustomerController {
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }

    @GetMapping("/index")
    public String loadForm(@RequestParam(required = false) String id, Model model) {
        try {
            if (id != null) {
                var idLong = ConvertorUtils.tryParseLong(id, -1L);
                if (idLong <= 0) {
                    return "redirect:/customer/index?error=Invalid+input+parameters";
                }

                var foundCustomer = _customerService.loadCustomer(idLong);
                model.addAttribute("customerDto", new CustomerDto());
                model.addAttribute("customerDtoList", foundCustomer);
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto(idLong));
            } else {
                var customerDtoList = _customerService.loadCustomers();
                model.addAttribute("customerDto", new CustomerDto());
                model.addAttribute("customerDtoList", customerDtoList);
                model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());
            }

            return "customer";
        } catch (BusinessException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @GetMapping("/index/{id}")
    public String loadFormById(@PathVariable String id, Model model) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        try {
            var foundCustomer = _customerService.loadCustomer(idLong);
            model.addAttribute("customerDto", foundCustomer);

            var customers = _customerService.loadCustomers();
            model.addAttribute("customerDtoList", customers);

            model.addAttribute("customerSearchInputDto", new CustomerSearchInputDto());

            return "customer";
        } catch (BusinessException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/addCustomer")
    public String addSubmit(@ModelAttribute CustomerDto customerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        try {
            _customerService.addOrEditCustomer(customerDto);

            return "redirect:/customer/index";
        } catch (BusinessException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/deleteCustomer/{id}")
    public String deleteSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        try {
            _customerService.removeCustomer(idLong);

            return "redirect:/customer/index";
        } catch (BusinessException ex) {
            return "redirect:/customer/index?error=" + ex.getEncodedMessage();
        } catch (Exception ex) {
            return "redirect:/customer/index?error=unhandled+error+occurred";
        }
    }

    @PostMapping("/editCustomer/{id}")
    public String editSubmit(@PathVariable String id) {
        var idLong = ConvertorUtils.tryParseLong(id, -1L);
        if (idLong <= 0) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        return "redirect:/customer/index/" + idLong;
    }

    @PostMapping("/searchCustomer")
    public String searchSubmit(@ModelAttribute CustomerSearchInputDto customerSearchInputDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/customer/index?error=Invalid+input+parameters";
        }

        var customerId = customerSearchInputDto.getCustomerId();
        if (customerId == null) {
            return "redirect:/customer/index";
        }

        return "redirect:/customer/index?search=true&id=" + customerId;
    }
}
