package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;
import com.tosan.model.TransactionTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class TransactionDto extends BaseDto {
    private Long id;
    private BigDecimal amount;
    private TransactionTypes transactionType;
    private LocalDateTime regDate;
    private String description;
    private Long accountId;
    private Long userId;
    private String traceNo;
    private Currencies currency;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;

    public TransactionDto(BigDecimal amount, TransactionTypes transactionType, String description,
                          Long accountId, Long userId, String traceNo, Currencies currency) {
        setAmount(amount);
        setTransactionType(transactionType);
        setDescription(description);
        setAccountId(accountId);
        setUserId(userId);
        setTraceNo(traceNo);
        setCurrency(currency);
    }
}