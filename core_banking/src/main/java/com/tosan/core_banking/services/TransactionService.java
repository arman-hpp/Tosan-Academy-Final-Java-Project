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
public class TransactionService implements ITransactionService {
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

    public Map<Integer, String> loadTransactionTypes() {
        return EnumUtils.getEnumNames(TransactionTypes.class);
    }

    public List<TransactionDto> loadTransactions() {
        var transactions = _transactionRepository.findAll();
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    public List<TransactionDto> loadTransactions(LocalDateTime fromDate, LocalDateTime toDate) {
        var transactions = _transactionRepository.findByRegDateBetweenOrderByRegDate(fromDate, toDate);
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    public TransactionDto loadTransactionByTraceNo(String traceNo) {
        var transaction = _transactionRepository.findByTraceNo(traceNo).orElse(null);
        if(transaction == null)
            throw new BusinessException("Can not find the transaction");

        return _modelMapper.map(transaction, TransactionDto.class);
    }

    public List<TransactionDto> loadLastAccountTransactions(Long accountId) {
        var transactions = _transactionRepository.findTop5ByAccountIdOrderByRegDateDesc(accountId);
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    public List<TransactionDto> loadLastBranchTransactions() {
        var transactions = _transactionRepository.findTop5ByOrderByRegDateDesc();
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    public List<TransactionDto> loadUserTransactions(Long userId) {
        var transactions = _transactionRepository.findByUserIdOrderByRegDateDesc(userId);
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    @Transactional
    public void transfer(TransferDto inputDto) {
        var srcTransaction = new TransactionDto(inputDto.getAmount(), TransactionTypes.Debit,
                LocalDateTime.now(), inputDto.getSrcDescription(), inputDto.getSrcAccountId(),
                inputDto.getUserId(), inputDto.getSrcTraceNo(), inputDto.getCurrency());

        var desTransaction = new TransactionDto(inputDto.getAmount(), TransactionTypes.Credit,
                LocalDateTime.now(), inputDto.getDesDescription(), inputDto.getDesAccountId(),
                inputDto.getUserId(), inputDto.getDesTraceNo(), inputDto.getCurrency());

        doTransaction(srcTransaction);
        doTransaction(desTransaction);
    }

    @Transactional
    public void doTransaction(TransactionDto inputDto) {
        var account = _accountRepository.findById(inputDto.getAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("can not find the account");

        if(account.getCurrency() != inputDto.getCurrency())
            throw new BusinessException("The currency of the account and the transaction do not match");

        if(inputDto.getTransactionType() == TransactionTypes.Credit) {
            account.setBalance(account.getBalance().add(inputDto.getAmount()));
        } else if (inputDto.getTransactionType() == TransactionTypes.Debit) {
            if(account.getBalance().compareTo(inputDto.getAmount()) < 0)
                throw new BusinessException("account balance is not enough!");

            account.setBalance(account.getBalance().subtract(inputDto.getAmount()));
        } else
            throw new BusinessException("transaction type is invalid");

        var transaction = _modelMapper.map(inputDto, Transaction.class);
        transaction.setRegDate(LocalDateTime.now());
        transaction.setAccount(account);

        if(StringUtils.isNullOrEmpty(transaction.getTraceNo())) {
            transaction.setTraceNo(_traceNoGenerator.generate());
        }

        _accountRepository.save(account);
        _transactionRepository.save(transaction);
    }
}
