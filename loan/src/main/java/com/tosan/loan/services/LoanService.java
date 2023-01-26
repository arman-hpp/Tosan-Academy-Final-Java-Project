package com.tosan.loan.services;

import com.tosan.loan.interfaces.ILoanService;
import com.tosan.repository.LoanRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanService implements ILoanService {
    private final LoanRepository _LoanRepository;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository, ModelMapper modelMapper) {
        _LoanRepository = loanRepository;
        _modelMapper = modelMapper;
    }

    public void loanLoans() {

    }

    public void loadLoan(Long loanId) {

    }

    public void addLoan() {

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
