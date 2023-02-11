package com.tosan.core_banking.dtos;

import com.tosan.dtos.BaseDto;
import com.tosan.model.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class UserDto extends BaseDto {
    private Long id;
    private String username;
    private String password;
    private UserTypes userType;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime lastLoginDate;
    @DateTimeFormat(pattern ="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime lastPasswordChangedDate;
}
