package com.tosan.core_banking;

import com.tosan.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository _accountRepository;
    private final ModelMapper _modelMapper;

    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _modelMapper = modelMapper;
    }

    public void loadAccounts() {

    }

    public void loadAccount(Long id) {

    }

    public void loadAccountByAccountNo(Long accountNo) {

    }

    public void loadBankAccount() {

    }

    public void addAccount() {

    }
}
