package com.tosan.repository;

import com.tosan.model.Customer;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
}
