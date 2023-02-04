package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class AccountSearchInputDto extends BaseDto {
    private Long accountId;
}
