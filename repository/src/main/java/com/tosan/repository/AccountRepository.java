package com.tosan.repository;

import com.tosan.model.*;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByAccountTypeAndCurrency(AccountTypes accountType, Currencies currency);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByIdAndCustomerId(Long accountId, Long customerId);
}
