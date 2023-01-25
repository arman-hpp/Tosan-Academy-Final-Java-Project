package com.tosan.core_banking.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDto extends BaseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
}