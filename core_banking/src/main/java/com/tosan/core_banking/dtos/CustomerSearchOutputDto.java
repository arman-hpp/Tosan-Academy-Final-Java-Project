package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class CustomerSearchOutputDto extends BaseDto {
    private Long customerId;
    private String customerName;
}
