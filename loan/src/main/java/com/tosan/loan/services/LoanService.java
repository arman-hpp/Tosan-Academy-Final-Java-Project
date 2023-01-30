package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanService;
import com.tosan.model.*;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoanService implements ILoanService {
    private final LoanRepository _loanRepository;
    private final LoanConditionsValidator _loanConditionsValidator;
    private final AccountRepository _accountRepository;
    private final CustomerRepository _customerRepository;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository,
                       LoanConditionsValidator loanConditionsValidator,
                       AccountRepository accountRepository,
                       CustomerRepository customerRepository, ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _loanConditionsValidator = loanConditionsValidator;
        _accountRepository = accountRepository;
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    public List<LoanDto> loanLoans() {
        var loans = _loanRepository.findAll();
        var outputDto = new ArrayList<LoanDto>();
        for(var loan : loans) {
            outputDto.add(_modelMapper.map(loan, LoanDto.class));
        }

        return outputDto;
    }

    public LoanDto loadLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        return _modelMapper.map(loan, LoanDto.class);
    }

    public void addLoan(LoanDto inputDto) {
        _loanConditionsValidator.validate(inputDto);

        var account = _accountRepository.findById(inputDto.getDepositAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("the deposit account is not exists");

        var customer = _customerRepository.findById(inputDto.getCustomerId()).orElse(null);
        if(customer == null)
            throw new BusinessException("the customer is not exists");

        var loan = _modelMapper.map(inputDto, Loan.class);
        loan.setCustomer(customer);
        loan.setDepositAccount(account);

        _loanRepository.save(loan);
    }

    public void editLoan(LoanDto inputDto) {
        _loanConditionsValidator.validate(inputDto);

        var account = _accountRepository.findById(inputDto.getDepositAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("the deposit account is not exists");

        var customer = _customerRepository.findById(inputDto.getCustomerId()).orElse(null);
        if(customer == null)
            throw new BusinessException("the customer is not exists");

        var loan = _loanRepository.findById(inputDto.getId()).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getDepositDate() != null)
            throw new BusinessException("the loan has been paid and it cannot be edit");

        _modelMapper.map(inputDto, loan);
        loan.setCustomer(customer);
        loan.setDepositAccount(account);

        _loanRepository.save(loan);
    }

    public void addOrEditLoan(LoanDto inputDto) {
        if(inputDto.getId()  == null || inputDto.getId() <= 0) {
            addLoan(inputDto);
        }
        else {
            editLoan(inputDto);
        }
    }

    public void loadLoanInterests() {

    }
}
