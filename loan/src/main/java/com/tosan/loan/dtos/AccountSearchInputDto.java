package com.tosan.loan.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class AccountSearchInputDto extends BaseDto {
    private Long customerId;

    private Long accountId;
}
