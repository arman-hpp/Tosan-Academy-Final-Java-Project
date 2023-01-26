package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanDto extends BaseDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer refundDuration;
    private Long customerId;
    private LocalDateTime requestDate;
    private LocalDateTime depositDate;
    private LocalDate firstPaymentDate;
    private Long depositAccountId;
}
