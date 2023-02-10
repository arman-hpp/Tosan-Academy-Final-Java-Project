package com.tosan.loan.services;

import com.tosan.core_banking.services.AccountService;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.dtos.LoanInterestStatisticsDto;
import com.tosan.loan.interfaces.ILoanValidator;
import com.tosan.model.Account;
import com.tosan.model.Customer;
import com.tosan.model.DomainException;
import com.tosan.model.Loan;
import com.tosan.repository.InstallmentRepository;
import com.tosan.repository.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LoanService {
    private final LoanRepository _loanRepository;
    private final InstallmentRepository _installmentRepository;
    private final ILoanValidator _loanValidator;
    private final LoanConditionsService _loanConditionsService;
    private final AccountService _accountService;
    private final ModelMapper _modelMapper;

    public LoanService(LoanRepository loanRepository,
                       InstallmentRepository installmentRepository,
                       ILoanValidator loanValidator,
                       LoanConditionsService loanConditionsService,
                       AccountService accountService, ModelMapper modelMapper) {
        _loanRepository = loanRepository;
        _installmentRepository = installmentRepository;
        _loanValidator = loanValidator;
        _loanConditionsService = loanConditionsService;
        _accountService = accountService;
        _modelMapper = modelMapper;
    }

    public List<LoanDto> loadLoans() {
        var loans = _loanRepository.findLoanWithDetails();
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            var account = loan.getAccount();
            var customer = loan.getCustomer();

            loanDto.setAccountId(account.getId());
            loanDto.setAccountCurrency(account.getCurrency());
            loanDto.setAccountBalance(account.getBalance());
            loanDto.setCustomerId(customer.getId());
            loanDto.setAccountCustomerName(customer.getFullName());

            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByCustomerId(Long customerId) {
        var loans = _loanRepository.findByCustomerIdOrderByRequestDate(customerId);
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            loanDto.setAccountId(loan.getAccount().getId());
            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public List<LoanDto> loadLoansByAccountId(Long accountId) {
        var loans = _loanRepository.findByAccountIdOrderByRequestDate(accountId);
        var loanDtoList = new ArrayList<LoanDto>();
        for (var loan : loans) {
            var loanDto = _modelMapper.map(loan, LoanDto.class);
            loanDto.setAccountId(accountId);
            loanDtoList.add(loanDto);
        }

        return loanDtoList;
    }

    public LoanDto loadLoan(Long loanId) {
        var loan = _loanRepository.findLoanByIdWithDetails(loanId).orElse(null);
        if (loan == null)
            throw new DomainException("error.loan.noFound");

        var loanDto = _modelMapper.map(loan, LoanDto.class);
        var account = loan.getAccount();
        var customer = loan.getCustomer();
        loanDto.setAccountId(account.getId());
        loanDto.setAccountBalance(account.getBalance());
        loanDto.setAccountCurrency(account.getCurrency());
        loanDto.setCustomerId(customer.getId());
        loanDto.setAccountCustomerName(customer.getFullName());

        return loanDto;
    }

    public void addLoan(LoanDto loanDto) {
        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var loan = _modelMapper.map(loanDto, Loan.class);

        var bankAccount = _accountService.loadBankAccount(loan.getCurrency());
        if (bankAccount.getBalance().compareTo(loan.getAmount()) < 0)
            throw new DomainException("error.loan.deposit.bankAccount.balance.notEnough");

        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setAccount(new Account(loanDto.getAccountId()));
        loan.setRequestDate(LocalDateTime.now());
        loan.setInterestRate(loanConditionsDto.getInterestRate());
        loan.setPaid(false);

        _loanRepository.save(loan);
    }

    public void editLoan(LoanDto loanDto) {
        var loanConditionsDto = _loanConditionsService.loadLoanCondition(loanDto.getCurrency());
        _loanValidator.validate(loanConditionsDto, loanDto);

        var loan = _loanRepository.findById(loanDto.getId()).orElse(null);
        if (loan == null)
            throw new DomainException("error.loan.noFound");

        if (loan.getPaid())
            throw new DomainException("error.loan.update.paidLoan");

        loan.setRefundDuration(loanDto.getRefundDuration());
        loan.setAmount(loanDto.getAmount());
        loan.setCustomer(new Customer(loanDto.getCustomerId()));
        loan.setAccount(new Account(loanDto.getAccountId()));

        _loanRepository.save(loan);
    }

    public void addOrEditLoan(LoanDto loanDto) {
        if (loanDto.getId() == null || loanDto.getId() <= 0) {
            addLoan(loanDto);
        } else {
            editLoan(loanDto);
        }
    }

    public void removeLoan(Long loanId) {
        var loan = _loanRepository.findById(loanId).orElse(null);
        if (loan == null)
            throw new DomainException("error.loan.noFound");

        if (loan.getPaid())
            throw new DomainException("error.loan.remove.paidLoan");

        _loanRepository.delete(loan);
    }

    @Async
    public CompletableFuture<List<LoanInterestStatisticsDto>> loadLoanSumInterests(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        var loanInterestStatistics = _installmentRepository.sumTotalInterests(fromDateTime, toDateTime);
        var loanInterestStatisticsDtoList = new ArrayList<LoanInterestStatisticsDto>();
        for (var loanInterestStatistic : loanInterestStatistics) {
            loanInterestStatisticsDtoList.add(_modelMapper.map(loanInterestStatistic, LoanInterestStatisticsDto.class));
        }

        return CompletableFuture.completedFuture(loanInterestStatisticsDtoList);
    }
}