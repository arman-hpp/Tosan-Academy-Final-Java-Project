package com.tosan.repository;

import com.tosan.model.Loan;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    Optional<Loan> findByLoanNo(Long loanNo);
}
