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
    private final LoanConditionsService _loanConditionsService;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository, LoanConditionsService loanConditionsService, ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _loanConditionsService = loanConditionsService;
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
        _loanConditionsService.validateLoanConditions(inputDto);
        var loan = _modelMapper.map(inputDto, Loan.class);
        _loanRepository.save(loan);
    }

    public void editLoan(LoanDto inputDto) {
        _loanConditionsService.validateLoanConditions(inputDto);

        var loan = _loanRepository.findById(inputDto.getId()).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getDepositDate() != null)
            throw new BusinessException("the loan has been paid and it cannot be edit");

        _modelMapper.map(inputDto, loan);
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
