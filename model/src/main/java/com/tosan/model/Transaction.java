package com.tosan.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Transactions")
public class Transaction extends BaseEntity {
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TransactionTypes transactionType;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trace_no", length = 36, nullable = false, unique = true)
    private String traceNo;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currencies currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}