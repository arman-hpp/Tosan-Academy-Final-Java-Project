package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.*;
import com.tosan.model.*;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DepositLoanService implements IDepositLoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final LoanConditionsValidatorService _loanConditionValidatorService;
    private final TransactionService _transactionService;
    private final AccountService _accountService;
    private final ILoanCalculator _loanCalculator;
    private final ModelMapper _modelMapper;

    public DepositLoanService(LoanRepository loanRepository,
                              InstallmentRepository installmentRepository,
                              LoanConditionsValidatorService loanConditionValidatorService,
                              TransactionService transactionService,
                              AccountService accountService,
                              ILoanCalculator loanCalculator,
                              ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanConditionValidatorService = loanConditionValidatorService;
        _transactionService = transactionService;
        _accountService = accountService;
        _loanCalculator = loanCalculator;
        _modelMapper = modelMapper;
    }

    @Transactional
    public void depositLoan(Long userId, Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if(loan == null)
            throw new BusinessException("can not find the loan");

        if(loan.getDepositDate() != null)
            throw new BusinessException("the loan has been already paid");

        var loanDto = _modelMapper.map(loan, LoanDto.class);
        _loanConditionValidatorService.validate(loanDto);

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

        var bankAccountId = _accountService.loadBankAccount(loan.getCurrency()).getId();
        var customerAccountId = loan.getAccount().getId();

        var transferDto = new TransferDto(loan.getAmount(), "Transfer to customer account",
                "Transfer loan from bank account", bankAccountId, customerAccountId,
                userId, loan.getCurrency());

        _transactionService.transfer(transferDto);
    }
}
