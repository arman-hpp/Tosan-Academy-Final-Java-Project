package com.tosan.core_banking.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class UserChangePasswordInputDto extends BaseDto {
    private String username;
    private String oldPassword;
    private String newPassword;
}
