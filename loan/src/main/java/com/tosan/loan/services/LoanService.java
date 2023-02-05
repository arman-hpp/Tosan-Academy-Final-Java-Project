package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanService;
import com.tosan.model.*;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LoanService implements ILoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final LoanConditionsValidatorService _loanConditionsValidatorService;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository,
                       InstallmentRepository installmentRepository,
                       LoanConditionsValidatorService loanConditionsValidatorService,
                       ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanConditionsValidatorService = loanConditionsValidatorService;
        _modelMapper = modelMapper;
    }

    public List<LoanDto> loadLoans() {
        var loans = _loanRepository.findAllByOrderByIdDesc();
        var outputDto = new ArrayList<LoanDto>();
        for(var loan : loans) {
            outputDto.add(_modelMapper.map(loan, LoanDto.class));
        }

        return outputDto;
    }

    public List<LoanDto> loadLoansByCustomerId(Long customerId) {
        var loans = _loanRepository.findByCustomerIdOrderByRequestDate(customerId);
        var results = new ArrayList<LoanDto>();
        for(var loan : loans) {
            results.add(_modelMapper.map(loan, LoanDto.class));
        }

        return results;
    }

    public List<LoanDto> loadLoansByAccountId(Long accountId) {
        var loans = _loanRepository.findByAccountIdOrderByRequestDate(accountId);
        var results = new ArrayList<LoanDto>();
        for(var loan : loans) {
            results.add(_modelMapper.map(loan, LoanDto.class));
        }

        return results;
    }

    public LoanDto loadLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        return _modelMapper.map(loan, LoanDto.class);
    }

    public void addLoan(LoanDto loanDto) {
        _loanConditionsValidatorService.validate(loanDto);

        var loan = _modelMapper.map(loanDto, Loan.class);
        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setAccount(new Account(loanDto.getAccountId()));

        _loanRepository.save(loan);
    }

    public void editLoan(LoanDto loanDto) {
        _loanConditionsValidatorService.validate(loanDto);

        var loan = _loanRepository.findById(loanDto.getId()).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getPaid())
            throw new BusinessException("the loan has been paid and it cannot be edit");

        _modelMapper.map(loanDto, loan);
        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setAccount(new Account(loanDto.getAccountId()));

        _loanRepository.save(loan);
    }

    public void addOrEditLoan(LoanDto loanDto) {
        if(loanDto.getId()  == null || loanDto.getId() <= 0) {
            addLoan(loanDto);
        }
        else {
            editLoan(loanDto);
        }
    }

    public void removeLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getPaid())
            throw new BusinessException("can not remove the loan, because it deposited already");

        _loanRepository.delete(loan);
    }

    public List<LoanInterestStatisticsDto> loadLoanSumInterests(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        var loanInterestStatistics = _installmentRepository.sumTotalInterests(fromDateTime, toDateTime);
        var results = new ArrayList<LoanInterestStatisticsDto>();
        for(var loanInterestStatistic : loanInterestStatistics) {
            results.add(_modelMapper.map(loanInterestStatistic, LoanInterestStatisticsDto.class));
        }

        return results;
    }
}
