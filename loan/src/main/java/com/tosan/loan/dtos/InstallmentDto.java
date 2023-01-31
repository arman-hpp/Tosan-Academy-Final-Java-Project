package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class InstallmentDto extends BaseDto {
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal interestAmount;
    private BigDecimal principalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal loanBalanceAmount;
    private Currencies currency;
}
