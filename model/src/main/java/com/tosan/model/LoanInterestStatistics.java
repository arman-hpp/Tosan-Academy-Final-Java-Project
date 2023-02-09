package com.tosan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class LoanInterestStatistics {
    private Currencies currency;
    private BigDecimal sumInterest;
}
