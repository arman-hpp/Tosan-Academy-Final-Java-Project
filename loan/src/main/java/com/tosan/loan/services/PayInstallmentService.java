package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.*;
import com.tosan.model.*;
import com.tosan.repository.InstallmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final TransactionService _transactionService;
    private final AccountService _accountService;

    public PayInstallmentService(InstallmentRepository installmentRepository,
                                 TransactionService transactionService,
                                 AccountService accountService) {
        _installmentRepository = installmentRepository;
        _transactionService = transactionService;
        _accountService = accountService;
    }

    @Transactional
    public void payInstallments(Long loanId, Long accountId, Long userId, Integer payInstallmentCount) {
        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(payInstallmentCount, loanId, false);

        var sumInstallmentsAmount = installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (var installment : installments) {
            installment.setPaid(true);
            installment.setPaidDate(LocalDateTime.now());
        }

        _installmentRepository.saveAll(installments);

        var bankAccountId = _accountService.loadBankAccount().getId();

        var transferDto = new TransferDto(sumInstallmentsAmount, "Pay Installments",
                "Pay Installments", accountId, bankAccountId,
                userId);

        _transactionService.transfer(transferDto);
    }
}