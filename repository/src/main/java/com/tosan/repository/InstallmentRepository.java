package com.tosan.repository;

import com.tosan.model.Installment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends BaseRepository<Installment, Long> {
    List<Installment> findByLoanIdOrderByInstallmentNo(Long LoanId);
    List<Installment> findTopCountByLoanIdAndPaidOrderByInstallmentNo(Integer count, Long loanId, Boolean paid);
}

