package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.interfaces.*;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.*;
import com.tosan.repository.*;
import com.tosan.utils.*;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {
    private final TransactionRepository _transactionRepository;
    private final AccountRepository _accountRepository;
    private final ITraceNoGenerator _traceNoGenerator;
    private final ModelMapper _modelMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              ITraceNoGenerator traceNoGenerator,
                              ModelMapper modelMapper) {
        _transactionRepository = transactionRepository;
        _accountRepository = accountRepository;
        _traceNoGenerator = traceNoGenerator;
        _modelMapper = modelMapper;
    }

    public List<TransactionDto> loadTransactions() {
        var transactions = _transactionRepository.findAll();
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            transactionDtoList.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return transactionDtoList;
    }

    public List<TransactionDto> loadTransactions(LocalDateTime fromDate, LocalDateTime toDate) {
        var transactions = _transactionRepository.findByRegDateBetweenOrderByRegDate(fromDate, toDate);
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            transactionDtoList.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return transactionDtoList;
    }

    public TransactionDto loadTransactionByTraceNo(String traceNo) {
        var transaction = _transactionRepository.findByTraceNo(traceNo).orElse(null);
        if(transaction == null)
            throw new BusinessException("Can not find the transaction");

        return _modelMapper.map(transaction, TransactionDto.class);
    }

    public List<TransactionDto> loadLastAccountTransactions(Long accountId) {
        var transactions = _transactionRepository.findTop5ByAccountIdOrderByRegDateDesc(accountId);
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            transactionDtoList.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return transactionDtoList;
    }

    public List<TransactionDto> loadLastBranchTransactions() {
        var transactions = _transactionRepository.findTop5ByOrderByRegDateDesc();
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            transactionDtoList.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return transactionDtoList;
    }

    public List<TransactionDto> loadUserTransactions(Long userId) {
        var transactions = _transactionRepository.findUserTransactionsWithDetails(userId);
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            var transactionDto = _modelMapper.map(transaction, TransactionDto.class);
            var account = transaction.getAccount();
            var customer = account.getCustomer();
            transactionDto.setAccountCurrency(account.getCurrency());
            transactionDto.setAccountBalance(account.getBalance());
            transactionDto.setAccountCustomerName(customer.getFullName());
            transactionDto.setAccountId(account.getId());
            transactionDto.setCurrency(account.getCurrency());
            transactionDtoList.add(transactionDto);
        }

        return transactionDtoList;
    }

    @Transactional
    public void transfer(TransferDto transferDto) {
        var srcTransaction = new TransactionDto(transferDto.getAmount(), TransactionTypes.Debit,
                transferDto.getSrcDescription(), transferDto.getSrcAccountId(),
                transferDto.getUserId(), transferDto.getSrcTraceNo(), transferDto.getCurrency());

        var desTransaction = new TransactionDto(transferDto.getAmount(), TransactionTypes.Credit,
                transferDto.getDesDescription(), transferDto.getDesAccountId(),
                transferDto.getUserId(), transferDto.getDesTraceNo(), transferDto.getCurrency());

        doTransaction(srcTransaction);
        doTransaction(desTransaction);
    }

    @Transactional
    public void doTransaction(TransactionDto transactionDto) {
        var account = _accountRepository.findById(transactionDto.getAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("can not find the account");

        if(account.getCurrency() != transactionDto.getCurrency())
            throw new BusinessException("The currency of the account and the transaction do not match");

        if(transactionDto.getTransactionType() == TransactionTypes.Credit) {
            account.setBalance(account.getBalance().add(transactionDto.getAmount()));
        } else if (transactionDto.getTransactionType() == TransactionTypes.Debit) {
            if(account.getBalance().compareTo(transactionDto.getAmount()) < 0)
                throw new BusinessException("account balance is not enough!");

            account.setBalance(account.getBalance().subtract(transactionDto.getAmount()));
        } else
            throw new BusinessException("transaction type is invalid");

        var transaction = _modelMapper.map(transactionDto, Transaction.class);
        transaction.setRegDate(LocalDateTime.now());
        transaction.setAccount(account);

        if(StringUtils.isNullOrEmpty(transaction.getTraceNo())) {
            transaction.setTraceNo(_traceNoGenerator.generate());
        }

        _accountRepository.save(account);
        _transactionRepository.save(transaction);
    }
}
