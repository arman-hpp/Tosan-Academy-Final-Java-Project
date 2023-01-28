package com.tosan.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Loan_Configurations")
public class LoanConfiguration extends BaseEntity {
    @Column(name = "interest_rate", nullable = false, precision = 7, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "max_refund_duration", nullable = false)
    private Integer maxRefundDuration;

    @Column(name = "max_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;
}
