package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class TransactionDto extends BaseDto {
    private Long id;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private TransactionTypes transactionType;
    private LocalDateTime regDate;
    @NonNull
    private String description;
    @NonNull
    private Long accountId;
    @NonNull
    private Long userId;
    @NonNull
    private String traceNo;
    @NonNull
    private Currencies currency;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;
}