package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.TransactionTypes;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransferDto extends BaseDto {
    private BigDecimal amount;
    private String srcDescription;
    private String desDescription;
    private Long srcAccountId;
    private Long desAccountId;
    private Long userId;
}
