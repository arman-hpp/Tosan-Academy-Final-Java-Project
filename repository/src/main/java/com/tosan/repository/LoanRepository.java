package com.tosan.repository;

import com.tosan.model.Loan;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
}
