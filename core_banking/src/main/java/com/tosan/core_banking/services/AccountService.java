package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.AccountDto;
import com.tosan.core_banking.interfaces.IAccountService;
import com.tosan.exceptions.BusinessException;
import com.tosan.model.Account;
import com.tosan.model.AccountTypes;
import com.tosan.model.Currencies;
import com.tosan.model.Customer;
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
        var results = new ArrayList<AccountDto>();
        for(var account : accounts) {
            var accountDto = _modelMapper.map(account, AccountDto.class);
            var customer = account.getCustomer();
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());
            results.add(accountDto);
        }

        return results;
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
            throw new BusinessException("can not find the bank account");

        return _modelMapper.map(account, AccountDto.class);
    }

    public void addAccount(AccountDto accountDto) {
        if(accountDto.getCustomerId() == null) {
            throw new BusinessException("please select a customer to open the account");
        }

        var account = _modelMapper.map(accountDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        account.setCustomer(new Customer(accountDto.getCustomerId()));

        _accountRepository.save(account);
    }
}
