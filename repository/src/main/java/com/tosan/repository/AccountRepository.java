package com.tosan.repository;

import com.tosan.model.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Optional<Account> findByAccountTypeAndCurrency(AccountTypes accountType, Currencies currency);
}
