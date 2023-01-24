package com.tosan.repository;

import com.tosan.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    Optional<Customer> findByCustomerNo(Long customerNo);
}
