package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.IDepositLoanService;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class DepositLoanService implements IDepositLoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final LoanConditionsService _loanConditionsService;
    private final AmortizationCalculatorService _amortizationCalculatorService;
    private final TransactionService _transactionService;
    private final ModelMapper _modelMapper;

    public DepositLoanService(LoanRepository loanRepository, InstallmentRepository installmentRepository,
                              LoanConditionsService loanConditionsService, AmortizationCalculatorService amortizationCalculatorService,
                              TransactionService transactionService, ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanConditionsService = loanConditionsService;
        _amortizationCalculatorService = amortizationCalculatorService;
        _transactionService = transactionService;
        _modelMapper = modelMapper;
    }

    @Transactional
    public void depositLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getDepositDate() != null)
            throw new BusinessException("the loan has been already paid");

        var loanDto = _modelMapper.map(loan, LoanDto.class);
        _loanConditionsService.validateLoanConditions(loanDto);
        loan.setDepositDate(LocalDateTime.now());

        var scheduleDto = _amortizationCalculatorService.calculate(loanDto);
        for (var installmentsDto : scheduleDto.getInstallments()) {

        }

        //TODO: continue
    }
}
