package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.Installment;
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

    public BigDecimal sumNonPaidInstallment(Long loanId, Integer payInstallmentCount) {
        if (payInstallmentCount <= 0)
            throw new BusinessException("installment count is not valid");

        var installments = _installmentRepository
                .findTopCountByLoanIdAndPaidOrderByInstallmentNo(payInstallmentCount, loanId, false);

        if(payInstallmentCount > installments.size())
            throw new BusinessException("installment count is bigger than loan not paid installments");

        return installments.stream()
                .map(Installment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void payInstallments(Long loanId, Long accountId, Long userId, Integer payInstallmentCount) {
        if(payInstallmentCount <= 0)
            throw new BusinessException("installment count is not valid");

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

        var currency = installments.get(0).getCurrency();

        var bankAccountId = _accountService.loadBankAccount(currency).getId();

        var transferDto = new TransferDto(sumInstallmentsAmount, "Pay Installments",
                "Pay Installments", accountId, bankAccountId,
                userId, currency);

        _transactionService.transfer(transferDto);
    }
}