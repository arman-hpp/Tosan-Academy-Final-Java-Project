package com.tosan.repository;

import com.tosan.model.Installment;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepository extends BaseRepository<Installment, Long> {
}
