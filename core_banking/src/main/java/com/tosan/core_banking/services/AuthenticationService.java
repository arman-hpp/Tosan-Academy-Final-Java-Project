package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.UserDto;
import com.tosan.model.UserTypes;
import org.springframework.stereotype.Service;

import java.util.Optional;

// TODO: org.springframework.security

@Service
public class AuthenticationService {
    public Boolean isUserAuthenticated() {
        return true;
    }

    public Boolean isUserAdmin() {
        return false;
    }

    public UserDto loadCurrentUser() {
        var userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("arman");
        userDto.setUserType(UserTypes.User);

        return userDto;
    }

    public Optional<String> loadCurrentUsername() {
        return Optional.of("Arman");
    }

    public Optional<Long> loadCurrentUserId() {
        return Optional.of(1L);
    }
}
