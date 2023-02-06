package com.tosan.repository;

import com.tosan.model.Loan;
import com.tosan.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    @Query(value = "SELECT t FROM Loan t JOIN FETCH t.account a JOIN FETCH a.customer ORDER BY t.id DESC")
    List<Loan> findLoanWithDetails();
    List<Loan> findAllByOrderByIdDesc();
    List<Loan> findByCustomerIdOrderByRequestDate(Long customerId);
    List<Loan> findByAccountIdOrderByRequestDate(Long accountId);
}
