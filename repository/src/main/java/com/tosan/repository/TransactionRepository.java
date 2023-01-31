package com.tosan.repository;

import com.tosan.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    List<Transaction> findTop5ByAccountIdOrderByRegDateDesc(Long accountId);

    List<Transaction> findTop5ByOrderByRegDateDesc();

    List<Transaction> findByUserIdOrderByRegDateDesc(Long userId);

    List<Transaction> findByRegDateBetweenOrderByRegDate(LocalDateTime from, LocalDateTime to);

    Optional<Transaction> findByTraceNo(String traceNo);
}
