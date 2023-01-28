package com.tosan.loan.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class InstallmentsBookDto {
    private BigDecimal monthlyPaymentAmount;
    private BigDecimal overPaymentAmount;
    private List<InstallmentDto> installments;
}
