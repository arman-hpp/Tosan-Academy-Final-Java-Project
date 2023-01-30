package com.tosan.application;

import com.tosan.core_banking.dtos.*;
import com.tosan.core_banking.services.*;
import com.tosan.model.TransactionTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class)
class ApplicationTests {
    @Autowired
    public TransactionService transactionService;
    @Autowired
    public AccountService accountService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testDoTransaction(){
        var transactionAmount = BigDecimal.valueOf(1000L);
        var accountId = 1L;
        var traceNo = UUID.randomUUID().toString();
        var accountBeforeTransaction = accountService.loadAccount(accountId);

        try {
            transactionService.doTransaction(
                    new TransactionDto(transactionAmount, TransactionTypes.Credit, LocalDateTime.now(),
                            "Test", accountId, 1L, traceNo));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        var accountAfterTransaction = accountService.loadAccount(accountId);
        assertEquals(accountBeforeTransaction.getBalance().add(transactionAmount), accountAfterTransaction.getBalance());

        var transaction = transactionService.loadTransactionByTraceNo(traceNo);
        assertNotNull(transaction);
    }

    @Test
    public void testTransfer() {
        var transactionAmount = BigDecimal.valueOf(1000L);
        var srcAccountId = 1L;
        var desAccountId = 2L;
        var srcTraceNo = UUID.randomUUID().toString();
        var desTraceNo = UUID.randomUUID().toString();
        var srcAccountBeforeTransaction = accountService.loadAccount(srcAccountId);
        var desAccountBeforeTransaction = accountService.loadAccount(desAccountId);

        try {
            transactionService.transfer(
                    new TransferDto(transactionAmount, "From Arman", "To Bank",
                            srcAccountId, desAccountId, 1L, srcTraceNo, desTraceNo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        var srcAccountAfterTransaction = accountService.loadAccount(srcAccountId);
        assertEquals(srcAccountBeforeTransaction.getBalance().subtract(transactionAmount), srcAccountAfterTransaction.getBalance());

        var desAccountAfterTransaction = accountService.loadAccount(desAccountId);
        assertEquals(desAccountBeforeTransaction.getBalance().add(transactionAmount), desAccountAfterTransaction.getBalance());

        var srcTransaction = transactionService.loadTransactionByTraceNo(srcTraceNo);
        assertNotNull(srcTransaction);

        var desTransaction = transactionService.loadTransactionByTraceNo(desTraceNo);
        assertNotNull(desTransaction);
    }
}
