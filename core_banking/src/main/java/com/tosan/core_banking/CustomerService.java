package com.tosan.core_banking;

import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.exceptions.BankException;
import com.tosan.model.Customer;
import com.tosan.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository _customerRepository;
    private final ModelMapper _modelMapper;

    private CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    public List<CustomerOutputDto> loadCustomers() {
        var customers = _customerRepository.findAll();
        var outputDto = new ArrayList<CustomerOutputDto>();
        for(var customer : customers) {
            outputDto.add(_modelMapper.map(customer, CustomerOutputDto.class));
        }

        return outputDto;
    }

    public CustomerOutputDto loadCustomer(Long id) {
        var customer = _customerRepository.findById(id).orElse(null);
        if(customer == null)
            throw new BankException("Can not find the customer");

        return _modelMapper.map(customer, CustomerOutputDto.class);
    }

    public CustomerOutputDto loadCustomerByCustomerNo(Long customerNo) {
        var customer = _customerRepository.findByCustomerNo(customerNo).orElse(null);
        if(customer == null)
            throw new BankException("Can not find the customer");

        return _modelMapper.map(customer, CustomerOutputDto.class);
    }

    public void addCustomer(CustomerInputDto inputDto) {
        var customer = _modelMapper.map(inputDto, Customer.class);
        _customerRepository.save(customer);
    }

    public void editCustomer(Long customerId, CustomerInputDto inputDto) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BankException("Can not find the customer");

        _modelMapper.map(inputDto, customer);
        _customerRepository.save(customer);
    }

    public void removeCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BankException("Can not find the customer");

        _customerRepository.delete(customer);
    }
}
