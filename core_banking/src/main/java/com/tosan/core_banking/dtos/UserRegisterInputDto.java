package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class UserRegisterInputDto extends BaseDto {
    private String username;
    private String password;
    private String repeatPassword;
    private Boolean agreeTerms;

    public UserRegisterInputDto(String username) {
        setUsername(username);
    }
}
