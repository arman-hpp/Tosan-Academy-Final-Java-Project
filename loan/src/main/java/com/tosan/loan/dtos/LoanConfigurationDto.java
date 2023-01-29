package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanConfigurationDto extends BaseDto {
    private Long id;
    private BigDecimal interestRate;
    private Integer minRefundDuration;
    private Integer maxRefundDuration;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
}
