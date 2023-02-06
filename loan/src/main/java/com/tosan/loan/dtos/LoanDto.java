package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;

import lombok.*;

import java.math.BigDecimal;
import java.time.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class LoanDto extends BaseDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer refundDuration;
    private Long customerId;
    private LocalDateTime requestDate;
    private LocalDateTime depositDate;
    private LocalDate firstPaymentDate;
    private Currencies currency;
    private Boolean paid;
    private Long accountId;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;

    public LoanDto(BigDecimal amount, Integer refundDuration,
                   Long customerId, Long accountId, Currencies currency) {
        setAmount(amount);
        setRefundDuration(refundDuration);
        setCustomerId(customerId);
        setCurrency(currency);
        setAccountId(accountId);
    }
}
