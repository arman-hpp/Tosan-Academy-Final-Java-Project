package com.tosan.repository;

import com.tosan.model.Account;
import com.tosan.model.AccountTypes;
import com.tosan.model.Currencies;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByAccountTypeAndCurrency(AccountTypes accountType, Currencies currency);

    List<Account> findByCustomerIdOrderByIdDesc(Long customerId);

    List<Account> findByAccountType(AccountTypes accountType);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.customer ORDER BY a.id DESC")
    List<Account> findAllAccountsWithDetails();

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = ?1")
    Optional<Account> findAccountWithDetails(Long accountId);

    @Query(value = "SELECT a FROM Account a LEFT JOIN FETCH a.customer WHERE a.id = ?1")
    Optional<Account> findFromAllAccountWithDetails(Long accountId);
}
