package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.dtos.LoanInterestStatisticsDto;
import com.tosan.loan.interfaces.ILoanService;
import com.tosan.model.Account;
import com.tosan.model.Customer;
import com.tosan.model.Loan;
import com.tosan.repository.InstallmentRepository;
import com.tosan.repository.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        var loanDtoList = new ArrayList<LoanDto>();
        for(var loan : loans) {
            loanDtoList.add(_modelMapper.map(loan, LoanDto.class));
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByCustomerId(Long customerId) {
        var loans = _loanRepository.findByCustomerIdOrderByRequestDate(customerId);
        var loanDtoList = new ArrayList<LoanDto>();
        for(var loan : loans) {
            loanDtoList.add(_modelMapper.map(loan, LoanDto.class));
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByAccountId(Long accountId) {
        var loans = _loanRepository.findByAccountIdOrderByRequestDate(accountId);
        var loanDtoList = new ArrayList<LoanDto>();
        for(var loan : loans) {
            loanDtoList.add(_modelMapper.map(loan, LoanDto.class));
        }

        return loanDtoList;
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
        var loanInterestStatisticsDtoList = new ArrayList<LoanInterestStatisticsDto>();
        for(var loanInterestStatistic : loanInterestStatistics) {
            loanInterestStatisticsDtoList.add(_modelMapper.map(loanInterestStatistic, LoanInterestStatisticsDto.class));
        }

        return loanInterestStatisticsDtoList;
    }
}
