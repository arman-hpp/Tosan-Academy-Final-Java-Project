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
public final class UserChangePasswordInputDto extends BaseDto {
    private Long id;
    private String oldPassword;
    private String newPassword;
    private String repeatNewPassword;
}
