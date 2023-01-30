package com.tosan.loan.services;

import com.tosan.core_banking.services.TransactionService;
import com.tosan.repository.AccountRepository;
import com.tosan.repository.InstallmentRepository;
import com.tosan.repository.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PayInstallmentService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final LoanConditionsService _loanConditionsService;
    private final AmortizationCalculatorService _amortizationCalculatorService;
    private final TransactionService _transactionService;
    private final ModelMapper _modelMapper;
    private final AccountRepository _accountRepository;

    public PayInstallmentService(LoanRepository loanRepository, InstallmentRepository installmentRepository, LoanConditionsService loanConditionsService, AmortizationCalculatorService amortizationCalculatorService, TransactionService transactionService, ModelMapper modelMapper, AccountRepository accountRepository) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanConditionsService = loanConditionsService;
        _amortizationCalculatorService = amortizationCalculatorService;
        _transactionService = transactionService;
        _modelMapper = modelMapper;
        _accountRepository = accountRepository;
    }

    public void payInstallments(Long loanId, Long accountId, Integer payInstallmentCount) {
        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(payInstallmentCount, loanId, false);
    }
}
