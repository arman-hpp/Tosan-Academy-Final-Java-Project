package com.tosan.core_banking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerInputDto extends BaseDto {
    private String firstName;
    private String lastName;
    private String address;
}