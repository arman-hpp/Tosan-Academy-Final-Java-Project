package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.*;
import com.tosan.model.*;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DepositLoanService implements IDepositLoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final LoanConditionsValidator _loanConditionValidator;
    private final ILoanCalculator _loanCalculator;
    private final TransactionService _transactionService;
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public DepositLoanService(LoanRepository loanRepository,
                              InstallmentRepository installmentRepository,
                              LoanConditionsValidator loanConditionsValidator,
                              ILoanCalculator loanCalculatorService,
                              TransactionService transactionService,
                              AccountRepository accountRepository,
                              ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanConditionValidator = loanConditionsValidator;
        _loanCalculator = loanCalculatorService;
        _transactionService = transactionService;
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    @Transactional
    public void depositLoan(Long userId, Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getDepositDate() != null)
            throw new BusinessException("the loan has been already paid");

        var bankAccount = _accountRepository.findByAccountType(AccountTypes.BankAccount).orElse(null);
        if(bankAccount == null)
            throw new BusinessException("can not find bank account");

        var customerAccount = _accountRepository.findById(loan.getDepositAccount().getId()).orElse(null);
        if(customerAccount == null)
            throw new BusinessException("can not find customer account");

        var loanDto = _modelMapper.map(loan, LoanDto.class);
        _loanConditionValidator.validate(loanDto);

        var loanPaymentInfo = _loanCalculator.calculate(loanDto);
        var list = new ArrayList<Installment>();
        for (var installmentsDto : loanPaymentInfo.getInstallments()) {
            var installment = _modelMapper.map(installmentsDto, Installment.class);
            installment.setLoan(loan);
            installment.setPaid(false);
            installment.setPaidDate(null);

            list.add(installment);
        }

        loan.setFirstPaymentDate(list.get(0).getDueDate());
        loan.setDepositDate(LocalDateTime.now());

        _installmentRepository.saveAll(list);
        _loanRepository.save(loan);

        var transferDto = new TransferDto(loan.getAmount(), "Transfer to customer account",
                "Transfer loan from bank account", bankAccount.getId(), customerAccount.getId(),
                userId);

        _transactionService.transfer(transferDto);
    }
}
