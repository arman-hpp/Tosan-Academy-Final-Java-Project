package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanInterestStatisticsDto extends BaseDto {
    private Currencies currency;
    private BigDecimal sumInterest;
}
