package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class InstallmentDto extends BaseDto {
    private Integer monthNumber;
    private BigDecimal loanBalanceAmount;
    private BigDecimal debtPaymentAmount;
    private BigDecimal interestPaymentAmount;
    private BigDecimal paymentAmount;
    private BigDecimal additionalPaymentAmount;
    private LocalDate paymentDate;
}
