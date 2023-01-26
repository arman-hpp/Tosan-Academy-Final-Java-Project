package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanAmortizationDto extends BaseDto {
    private BigDecimal monthlyPaymentAmount;
    private BigDecimal overPaymentAmount;
    private List<InstallmentDto> monthlyPayments;
}
