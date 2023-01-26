package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.interfaces.ITransactionService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.Transaction;
import com.tosan.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService implements ITransactionService {
    private final TransactionRepository _transactionRepository;
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              ModelMapper modelMapper) {
        _transactionRepository = transactionRepository;
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    public List<TransactionDto> loadTransactions() {
        var transactions = _transactionRepository.findAll();
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
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

    public List<TransactionDto> loadLastBranchTransactions(Long userId) {
        var transactions = _transactionRepository.findByUserIdOrderByRegDateDesc(userId);
        var outputDto = new ArrayList<TransactionDto>();
        for(var transaction : transactions) {
            outputDto.add(_modelMapper.map(transaction, TransactionDto.class));
        }

        return outputDto;
    }

    @Transactional
    public void doTransaction(TransactionDto inputDto) {
        var account = _accountRepository.findById(inputDto.getAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the account");

        if(account.getBalance().compareTo(inputDto.getAmount()) < 0)
            throw new BusinessException("account balance is not enough!");

        account.setBalance(account.getBalance().add(inputDto.getAmount()));

        var transaction = _modelMapper.map(inputDto, Transaction.class);
        transaction.setRegDate(LocalDateTime.now());

        _transactionRepository.save(transaction);
        _accountRepository.save(account);
    }
}
