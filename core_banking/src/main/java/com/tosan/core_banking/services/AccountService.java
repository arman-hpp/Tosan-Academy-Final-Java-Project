package com.tosan.core_banking.services;

import com.tosan.core_banking.exceptions.BankException;
import com.tosan.model.*;
import com.tosan.repository.AccountRepository;
import com.tosan.core_banking.dtos.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountService {
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    public List<AccountDto> loadAccounts() {
        var accounts = _accountRepository.findAll();
        var outputDto = new ArrayList<AccountDto>();
        for(var account : accounts) {
            outputDto.add(_modelMapper.map(account, AccountDto.class));
        }

        return outputDto;
    }

    public AccountDto loadAccount(Long accountId) {
        var account = _accountRepository.findById(accountId).orElse(null);
        if(account == null)
            throw new BankException("Can not find the account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public AccountDto loadBankAccount() {
        var account = _accountRepository.findByAccountType(AccountTypes.BankAccount).orElse(null);
        if(account == null)
            throw new BankException("Can not find the bank account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public void addAccount(AccountDto inputDto) {
        var account = _modelMapper.map(inputDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        _accountRepository.save(account);
    }
}
