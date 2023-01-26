package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class CustomerDto extends BaseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
}
