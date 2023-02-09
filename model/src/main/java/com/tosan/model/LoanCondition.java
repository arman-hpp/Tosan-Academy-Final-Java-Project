package com.tosan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Loan_Conditions")
public class LoanCondition extends BaseEntity {
    @Column(name = "interest_rate", nullable = false, precision = 7, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "min_refund_duration", nullable = false)
    private Integer minRefundDuration;

    @Column(name = "max_refund_duration", nullable = false)
    private Integer maxRefundDuration;

    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currencies currency;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;
}
