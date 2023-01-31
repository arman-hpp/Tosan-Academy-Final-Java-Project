package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class TransactionDto extends BaseDto {
    private BigDecimal amount;
    private TransactionTypes transactionType;
    private LocalDateTime regDate;
    private String description;
    private Long accountId;
    private Long userId;
    private String traceNo;
    private Currencies currency;
}
