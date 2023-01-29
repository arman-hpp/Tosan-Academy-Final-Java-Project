package com.tosan.application;

import com.tosan.core_banking.dtos.TransactionDto;
import com.tosan.core_banking.dtos.TransferDto;
import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.model.TransactionTypes;
import com.tosan.repository.AccountRepository;
import com.tosan.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class)
class ApplicationTests {
    @Autowired
    public TransactionService transactionService;
    @Autowired
    public AccountService accountService;

    @Test
    void contextLoads() {
//        try {
//            transactionService.doTransaction(
//                    new TransactionDto(null, new BigDecimal(1000L), TransactionTypes.Credit, LocalDateTime.now(), "Test", 1L, 1L));
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }

        try {
            transactionService.transfer(
                    new TransferDto(BigDecimal.valueOf(2000), "From Arman", "To Bank", 1l, 2L, 1L ));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        var transactions = transactionService.loadTransactions();
        System.out.println("transactions count:" + transactions.size());

        var srcAccount = accountService.loadAccount(1L);
        System.out.println("account balance: " + srcAccount.getBalance());

        var desAccount = accountService.loadAccount(2L);
        System.out.println("account balance: " + desAccount.getBalance());
    }
}
