package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.AccountDto;
import com.tosan.core_banking.interfaces.IAccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.*;
import com.tosan.repository.AccountRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService implements IAccountService {
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    public List<AccountDto> loadAccounts() {
        var accounts = _accountRepository.findAllAccountsWithDetails();
        var outputDto = new ArrayList<AccountDto>();
        for(var account : accounts) {
            var accountDto = _modelMapper.map(account, AccountDto.class);
            var customer = account.getCustomer();
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());
            outputDto.add(accountDto);
        }

        return outputDto;
    }

    public AccountDto loadAccount(Long accountId) {
        var account = _accountRepository.findAccountWithDetails(accountId).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the account");

        var accountDto = _modelMapper.map(account, AccountDto.class);

        var customer = account.getCustomer();
        accountDto.setCustomerId(customer.getId());
        accountDto.setCustomerName(customer.getFullName());

        return accountDto;
    }

    public AccountDto loadBankAccount(Currencies currency) {
        var account = _accountRepository.findByAccountTypeAndCurrency(AccountTypes.BankAccount, currency).orElse(null);
        if(account == null)
            throw new BusinessException("Can not find the bank account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public List<AccountDto> searchAccounts(Long accountId, Long customerId) {
        var outputDto = new ArrayList<AccountDto>();

        if(accountId != null) {
            if(customerId != null) {
                var accounts = _accountRepository.findAccountsWithDetails(accountId, customerId);
                for(var account : accounts) {
                    var accountDto = _modelMapper.map(account, AccountDto.class);
                    var customer = account.getCustomer();
                    accountDto.setCustomerId(customer.getId());
                    accountDto.setCustomerName(customer.getFullName());
                    outputDto.add(accountDto);
                }
            }
            else {
                var account = _accountRepository.findAccountWithDetails(accountId).orElse(null);
                if(account != null) {
                    var accountDto = _modelMapper.map(account, AccountDto.class);
                    var customer = account.getCustomer();
                    accountDto.setCustomerId(customer.getId());
                    accountDto.setCustomerName(customer.getFullName());
                    outputDto.add(_modelMapper.map(account, AccountDto.class));
                }
            }

            return outputDto;
        }
        else {
            if(customerId != null) {
                var accounts = _accountRepository.findByCustomerId(customerId);
                for(var account : accounts) {
                    var accountDto = _modelMapper.map(account, AccountDto.class);
                    var customer = account.getCustomer();
                    accountDto.setCustomerId(customer.getId());
                    accountDto.setCustomerName(customer.getFullName());
                    outputDto.add(accountDto);
                }

                return outputDto;
            }
            else {
                return loadAccounts();
            }
        }
    }

    public void addAccount(AccountDto inputDto) {
        var account = _modelMapper.map(inputDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        account.setCustomer(new Customer(inputDto.getCustomerId()));

        _accountRepository.save(account);
    }
}
