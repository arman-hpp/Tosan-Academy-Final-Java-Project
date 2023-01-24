package com.tosan.core_banking;

import com.tosan.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository _transactionRepository;
    private final ModelMapper _modelMapper;

    public TransactionService(TransactionRepository transactionRepository, ModelMapper modelMapper) {
        _transactionRepository = transactionRepository;
        _modelMapper = modelMapper;
    }

    public void doTransaction() {

    }

    public void loadLastAccountTransactions() {

    }

    public void loadLastBranchTransactions() {

    }

    public void loadAllTransactions() {

    }
}
