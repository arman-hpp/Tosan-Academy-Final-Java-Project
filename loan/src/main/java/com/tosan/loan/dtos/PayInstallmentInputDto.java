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
public final class PayInstallmentInputDto extends BaseDto {
    private Long loanId;
    private Long accountId;
    private Integer InstallmentCount;
    private BigDecimal Amount;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;
}
