package com.tosan.repository;

import com.tosan.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    List<Transaction> findTop5ByAccountIdOrderByRegDateDesc(Long accountId);

    List<Transaction> findTop5ByOrderByRegDateDesc();

    List<Transaction> findByUserIdOrderByRegDateDesc(Long userId);

    Optional<Transaction> findByTraceNo(String traceNo);
}
