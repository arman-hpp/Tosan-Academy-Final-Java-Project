package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.CustomerDto;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.Customer;
import com.tosan.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository _customerRepository;
    private final ModelMapper _modelMapper;

    private CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    public List<CustomerDto> loadCustomers() {
        var customers = _customerRepository.findAllByOrderByIdDesc();
        var customerDtoList = new ArrayList<CustomerDto>();
        for(var customer : customers) {
            customerDtoList.add(_modelMapper.map(customer, CustomerDto.class));
        }

        return customerDtoList;
    }

    public CustomerDto loadCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BusinessException("can not find the customer");

        return _modelMapper.map(customer, CustomerDto.class);
    }

    public void addCustomer(CustomerDto customerDto) {
        var customer = _modelMapper.map(customerDto, Customer.class);
        _customerRepository.save(customer);
    }

    public void editCustomer(CustomerDto customerDto) {
        var customer = _customerRepository.findById(customerDto.getId()).orElse(null);
        if(customer == null)
            throw new BusinessException("can not find the customer");

        _modelMapper.map(customerDto, customer);
        _customerRepository.save(customer);
    }

    public void addOrEditCustomer(CustomerDto customerDto) {
        if(customerDto.getId()  == null || customerDto.getId() <= 0) {
            addCustomer(customerDto);
        }
        else {
            editCustomer(customerDto);
        }
    }

    public void removeCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BusinessException("can not find the customer");

        _customerRepository.delete(customer);
    }
}
