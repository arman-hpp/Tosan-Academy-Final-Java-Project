package com.tosan.repository;

import com.tosan.model.*;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByAccountTypeAndCurrency(AccountTypes accountType, Currencies currency);

    List<Account> findByCustomerId(Long customerId);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.customer ORDER BY a.id DESC")
    List<Account> findAllAccountsWithDetails();

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = ?1 AND a.customer.id = ?2")
    List<Account> findAccountsWithDetails(Long accountId, Long customerId);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = ?1")
    Optional<Account> findAccountWithDetails(Long accountId);
}
