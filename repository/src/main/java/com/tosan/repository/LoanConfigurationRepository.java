package com.tosan.repository;

import com.tosan.model.LoanCondition;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanConfigurationRepository extends BaseRepository<LoanCondition, Long> {
    Optional<LoanCondition> findTop1ByExpireDateIsNullOrderByStartDateDesc();
}
