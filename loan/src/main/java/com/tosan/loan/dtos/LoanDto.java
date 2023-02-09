package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.Currencies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime requestDate;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime depositDate;
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate firstPaymentDate;
    private Currencies currency;
    private Boolean paid;
    private Long accountId;
    private String accountCustomerName;
    private BigDecimal accountBalance;
    private Currencies accountCurrency;
}
