package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanConditionsDto extends BaseDto {
    private Long id;
    private BigDecimal interestRate;
    private Integer minRefundDuration;
    private Integer maxRefundDuration;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private Currencies currency;
}
