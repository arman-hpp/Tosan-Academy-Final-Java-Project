package com.tosan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "Customers")
public class Customer extends BaseEntity {
    @Column(name = "first_name", length = 40, nullable = false)
    private String firstName;

    @Column(name = "last_name",length = 80, nullable = false)
    private String lastName;

    @Column(name = "address",length = 100, nullable = false)
    private String address;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Loan> loans = new HashSet<>();

    public Customer() {
    }

    public Customer(Long Id) {
        setId(Id);
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
