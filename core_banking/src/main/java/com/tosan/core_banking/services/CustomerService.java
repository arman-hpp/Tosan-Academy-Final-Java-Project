package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.*;
import com.tosan.exceptions.BusinessException;
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

    public List<CustomerDto> loadCustomers() {
        var customers = _customerRepository.findAll();
        var outputDto = new ArrayList<CustomerDto>();
        for(var customer : customers) {
            outputDto.add(_modelMapper.map(customer, CustomerDto.class));
        }

        return outputDto;
    }

    public CustomerDto loadCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BusinessException("Can not find the customer");

        return _modelMapper.map(customer, CustomerDto.class);
    }

    public void addCustomer(CustomerDto inputDto) {
        var customer = _modelMapper.map(inputDto, Customer.class);
        _customerRepository.save(customer);
    }

    public void editCustomer(CustomerDto inputDto) {
        var customer = _customerRepository.findById(inputDto.getId()).orElse(null);
        if(customer == null)
            throw new BusinessException("Can not find the customer");

        _modelMapper.map(inputDto, customer);
        _customerRepository.save(customer);
    }

    public void addOrEditCustomer(CustomerDto inputDto) {
        if(inputDto.getId()  == null || inputDto.getId() <= 0) {
            addCustomer(inputDto);
        }
        else {
            editCustomer(inputDto);
        }
    }

    public void removeCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BusinessException("Can not find the customer");

        _customerRepository.delete(customer);
    }
}
