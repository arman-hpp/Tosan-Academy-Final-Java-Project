package com.tosan.repository;

import com.tosan.model.*;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanConditionsRepository extends BaseRepository<LoanCondition, Long> {
    Optional<LoanCondition> findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(Currencies currency);
}
