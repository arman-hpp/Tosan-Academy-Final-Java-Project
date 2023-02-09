package com.tosan.loan.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanPaymentInfoDto {
    private BigDecimal monthlyPaymentAmount;
    private BigDecimal overPaymentAmount;
    private List<InstallmentDto> installments;
}
