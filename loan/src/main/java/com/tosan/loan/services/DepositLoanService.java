package com.tosan.loan.services;

import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.ILoanCalculator;
import com.tosan.loan.interfaces.ILoanValidator;
import com.tosan.model.Installment;
import com.tosan.repository.InstallmentRepository;
import com.tosan.repository.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DepositLoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final ILoanValidator _loanValidator;
    private final LoanConditionsService _loanConditionsService;
    private final TransactionService _transactionService;
    private final AccountService _accountService;
    private final ILoanCalculator _loanCalculator;
    private final ModelMapper _modelMapper;

    public DepositLoanService(LoanRepository loanRepository,
                              InstallmentRepository installmentRepository,
                              ILoanValidator loanValidator,
                              LoanConditionsService loanConditionsService,
                              TransactionService transactionService,
                              AccountService accountService,
                              ILoanCalculator loanCalculator,
                              ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanValidator = loanValidator;
        _loanConditionsService = loanConditionsService;
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

        var bankAccount= _accountService.loadBankAccount(loan.getCurrency());
        if(bankAccount.getBalance().compareTo(loan.getAmount()) < 0)
            throw new BusinessException("bank account balance is not enough!");

        loan.setPaid(true);
        loan.setFirstPaymentDate(LocalDate.now().plusMonths(1));
        loan.setDepositDate(LocalDateTime.now());

        var loanDto = _modelMapper.map(loan, LoanDto.class);

        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var loanPaymentInfo = _loanCalculator.calculate(loanDto);
        var list = new ArrayList<Installment>();
        for (var installmentsDto : loanPaymentInfo.getInstallments()) {
            var installment = _modelMapper.map(installmentsDto, Installment.class);
            installment.setLoan(loan);
            installment.setPaid(false);
            installment.setPaidDate(null);

            list.add(installment);
        }

        _installmentRepository.saveAll(list);
        _loanRepository.save(loan);

        var bankAccountId = bankAccount.getId();
        var customerAccountId = loan.getAccount().getId();

        var transferDto = new TransferDto(loan.getAmount(), "Transfer to customer account",
                "Transfer loan from bank account", bankAccountId, customerAccountId,
                userId, loan.getCurrency());

        _transactionService.transfer(transferDto);
    }
}
