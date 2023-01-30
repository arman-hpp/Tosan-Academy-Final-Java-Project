package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.*;
import com.tosan.repository.AccountRepository;
import com.tosan.repository.InstallmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final TransactionService _transactionService;
    private final AccountRepository _accountRepository;

    public PayInstallmentService(InstallmentRepository installmentRepository,
                                 TransactionService transactionService,
                                 AccountRepository accountRepository) {
        _installmentRepository = installmentRepository;
        _transactionService = transactionService;
        _accountRepository = accountRepository;
    }

    @Transactional
    public void payInstallments(Long loanId, Long accountId, Long userId, Integer payInstallmentCount) {
        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(loanId, false, payInstallmentCount);

        var sumInstallmentsAmount = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var customerAccount = _accountRepository.findById(accountId).orElse(null);
        if(customerAccount == null)
            throw new BusinessException("can not find customer account");

        if(customerAccount.getBalance().compareTo(sumInstallmentsAmount) > 0)
            throw new BusinessException("the account balance is not enough");

        for (var installment : installments) {
            installment.setPaid(true);
            installment.setPaidDate(LocalDateTime.now());
        }

        _installmentRepository.saveAll(installments);

        var bankAccount = _accountRepository.findByAccountType(AccountTypes.BankAccount).orElse(null);
        if(bankAccount == null)
            throw new BusinessException("can not find bank account");

        var transferDto = new TransferDto(sumInstallmentsAmount, "Pay Installments",
                "Pay Installments", customerAccount.getId(), bankAccount.getId(),
                userId);

        _transactionService.transfer(transferDto);
    }
}