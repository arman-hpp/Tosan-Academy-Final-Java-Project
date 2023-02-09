package com.tosan.application;

import com.tosan.core_banking.services.AccountService;
import com.tosan.core_banking.services.TransactionService;
import com.tosan.model.AccountTypes;
import com.tosan.model.Currencies;
import com.tosan.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ContextConfiguration(classes = { Application.class })
@ActiveProfiles("test")
class ApplicationTests {
    @Autowired
    public TransactionService transactionService;

    @Autowired
    public AccountService accountService;

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public CustomerRepository customerRepository;

    @Autowired
    public LoanRepository loanRepository;

    @Autowired
    public InstallmentRepository installmentRepository;

    @Autowired
    public LoanConditionsRepository loanConditionsRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void testRepositories() {
        assertDoesNotThrow(() -> {
            accountRepository.findByAccountTypeAndCurrency(AccountTypes.BankAccount, Currencies.Rial);
            transactionRepository.findByTraceNo("Test");
            transactionRepository.findByRegDateWithDetails(LocalDateTime.MIN, LocalDateTime.MAX);
            transactionRepository.findTop5ByAccountIdOrderByRegDateDesc(1L);
            transactionRepository.findTop5ByOrderByRegDateDesc();
            transactionRepository.findUserTransactionsWithDetails(1L);
            loanRepository.findByAccountIdOrderByRequestDate(1L);
            loanRepository.findByCustomerIdOrderByRequestDate(1L);
            installmentRepository.findByLoanIdOrderByInstallmentNo(1L);
            installmentRepository.findTopCountByLoanIdAndPaidOrderByInstallmentNo(5, 1L, true);
            installmentRepository.findByLoanIdAndPaidTrueOrderByInstallmentNo(1L);
            installmentRepository.findByLoanIdAndPaidFalseOrderByInstallmentNo(1L);
            installmentRepository.sumTotalInterests(LocalDateTime.MIN, LocalDateTime.MAX);
            loanConditionsRepository.findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(Currencies.Rial);
            userRepository.findByUsername("arman");
            accountRepository.findAccountWithDetails(1L);
            accountRepository.findAllAccountsWithDetails();
        });
    }
//
//    @Test
//    public void testDoTransaction(){
//        var transactionAmount = BigDecimal.valueOf(1000L);
//        var accountId = 1L;
//        var traceNo = UUID.randomUUID().toString();
//        var accountBeforeTransaction = accountService.loadAccount(accountId);
//
//        assertDoesNotThrow(() -> {
//            transactionService.doTransaction(
//                    new TransactionDto(transactionAmount, TransactionTypes.Credit, LocalDateTime.now(),
//                            "Test", accountId, 1L, traceNo, Currencies.rial));
//        });
//
//        var accountAfterTransaction = accountService.loadAccount(accountId);
//        assertEquals(accountBeforeTransaction.getBalance().add(transactionAmount), accountAfterTransaction.getBalance());
//
//        var transaction = transactionService.loadTransactionByTraceNo(traceNo);
//        assertNotNull(transaction);
//    }
//
//    @Test
//    public void testTransfer() {
//        var transactionAmount = BigDecimal.valueOf(1000L);
//        var srcAccountId = 1L;
//        var desAccountId = 2L;
//        var srcTraceNo = UUID.randomUUID().toString();
//        var desTraceNo = UUID.randomUUID().toString();
//        var srcAccountBeforeTransaction = accountService.loadAccount(srcAccountId);
//        var desAccountBeforeTransaction = accountService.loadAccount(desAccountId);
//
//        assertDoesNotThrow(() -> {
//            transactionService.transfer(
//                    new TransferDto(transactionAmount, "From Arman", "To Bank",
//                            srcAccountId, desAccountId, 1L, Currencies.rial, srcTraceNo, desTraceNo));
//        });
//
//        var srcAccountAfterTransaction = accountService.loadAccount(srcAccountId);
//        assertEquals(srcAccountBeforeTransaction.getBalance().subtract(transactionAmount), srcAccountAfterTransaction.getBalance());
//
//        var desAccountAfterTransaction = accountService.loadAccount(desAccountId);
//        assertEquals(desAccountBeforeTransaction.getBalance().add(transactionAmount), desAccountAfterTransaction.getBalance());
//
//        var srcTransaction = transactionService.loadTransactionByTraceNo(srcTraceNo);
//        assertNotNull(srcTransaction);
//
//        var desTransaction = transactionService.loadTransactionByTraceNo(desTraceNo);
//        assertNotNull(desTransaction);
//    }
//
}
