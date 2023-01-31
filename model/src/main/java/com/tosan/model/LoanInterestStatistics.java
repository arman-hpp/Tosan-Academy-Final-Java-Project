package com.tosan.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class LoanInterestStatistics {
    private Currencies currency;
    private BigDecimal sumInterest;
}
