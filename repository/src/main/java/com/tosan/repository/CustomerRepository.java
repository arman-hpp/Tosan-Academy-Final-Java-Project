package com.tosan.repository;

import com.tosan.model.Customer;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    List<Customer> findAllByOrderByIdDesc();
}
