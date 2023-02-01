package com.tosan.repository;

import com.tosan.model.Loan;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    List<Loan> findByCustomerIdOrderByRequestDate(Long customerId);

    List<Loan> findByDepositAccountIdOrderByRequestDate(Long depositAccountId);
}
