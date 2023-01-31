package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;

import com.tosan.model.Currencies;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class TransferDto extends BaseDto {
    @NonNull
    private BigDecimal amount;
    @NonNull
    private String srcDescription;
    @NonNull
    private String desDescription;
    @NonNull
    private Long srcAccountId;
    @NonNull
    private Long desAccountId;
    @NonNull
    private Long userId;
    @NonNull
    private Currencies currency;
    private String srcTraceNo;
    private String desTraceNo;
}
