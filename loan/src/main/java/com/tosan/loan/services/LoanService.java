package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.ILoanService;
import com.tosan.model.Account;
import com.tosan.model.AccountTypes;
import com.tosan.model.Loan;
import com.tosan.repository.LoanRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService implements ILoanService {
    private final LoanRepository _loanRepository;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository, ModelMapper modelMapper) {
        _loanRepository = loanRepository;
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
        var account = _loanRepository.findById(loanId).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the loan");

        return _modelMapper.map(account, LoanDto.class);
    }

    public void addLoan(LoanDto loanDto) {
        var loan = _modelMapper.map(loanDto, Loan.class);
        _loanRepository.save(loan);
    }

    public void depositLoan() {

    }

    public void loadLoanConfigs() {

    }

    public void editLoanConfigs() {

    }

    public void loadLoanInterests() {

    }
}
