package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.*;

import lombok.*;

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