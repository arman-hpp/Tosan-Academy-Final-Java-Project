package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class TransferDto extends BaseDto {
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
    private String srcTraceNo;
    private String desTraceNo;
}
