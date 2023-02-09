package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.AccountTypes;
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
public final class AccountDto extends BaseDto {
    private Long id;
    private BigDecimal balance;
    private Currencies currency;
    private AccountTypes accountType;
    private Long customerId;
    private String customerName;
}