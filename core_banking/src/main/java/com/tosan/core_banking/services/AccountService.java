package com.tosan.core_banking.services;

import com.tosan.core_banking.interfaces.IAccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.*;
import com.tosan.repository.AccountRepository;
import com.tosan.core_banking.dtos.*;
import com.tosan.utils.EnumUtils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountService implements IAccountService {
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    public Map<Integer, String> loadCurrencies() {
        return EnumUtils.GetEnumNames(Currencies.class);
    }

    public List<AccountDto> loadAccounts() {
        var accounts = _accountRepository.findAll();
        var outputDto = new ArrayList<AccountDto>();
        for(var account : accounts) {
            outputDto.add(_modelMapper.map(account, AccountDto.class));
        }

        return outputDto;
    }

    public List<AccountDto> searchAccounts(Long accountId, Long customerId) {
        var outputDto = new ArrayList<AccountDto>();

        if(accountId != null) {
            if(customerId != null) {
                var accounts = _accountRepository.findByIdAndCustomerId(accountId, customerId);
                for(var account : accounts) {
                    outputDto.add(_modelMapper.map(account, AccountDto.class));
                }
            }
            else {
                var account = _accountRepository.findById(accountId);
                outputDto.add(_modelMapper.map(account, AccountDto.class));
            }

            return outputDto;
        }
        else {
            if(customerId != null) {
                var accounts = _accountRepository.findByCustomerId(customerId);
                for(var account : accounts) {
                    outputDto.add(_modelMapper.map(account, AccountDto.class));
                }

                return outputDto;
            }
            else {
                return loadAccounts();
            }
        }
    }

    public AccountDto loadAccount(Long accountId) {
        var account = _accountRepository.findById(accountId).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public AccountDto loadBankAccount(Currencies currency) {
        var account = _accountRepository.findByAccountTypeAndCurrency(AccountTypes.BankAccount, currency).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the bank account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public void addAccount(AccountDto inputDto) {
        var account = _modelMapper.map(inputDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        _accountRepository.save(account);
    }
}
