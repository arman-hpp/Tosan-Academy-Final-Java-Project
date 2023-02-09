package com.tosan.core_banking.dtos;

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
public final class TransferDto extends BaseDto {
    private BigDecimal amount;
    private String srcDescription;
    private String desDescription;
    private Long srcAccountId;
    private Long desAccountId;
    private Long userId;
    private Currencies currency;
    private String srcTraceNo;
    private String desTraceNo;

    public TransferDto(BigDecimal amount, String srcDescription, String desDescription,
                       Long srcAccountId, Long desAccountId,  Long userId, Currencies currency) {
        setAmount(amount);
        setSrcDescription(srcDescription);
        setDesDescription(desDescription);
        setSrcAccountId(srcAccountId);
        setDesAccountId(desAccountId);
        setUserId(userId);
        setCurrency(currency);
    }
}
