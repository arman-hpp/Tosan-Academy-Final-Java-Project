package com.tosan.core_banking.services;

import com.tosan.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

// TODO: org.springframework.security

@Service
public class AuthenticationService {
    public Boolean isUserAuthenticated() {
        return true;
    }

    public User loadCurrentUser() {
        return new User();
    }

    public Optional<String> loadCurrentUsername() {
        return Optional.empty();
    }

    public Optional<Long> loadCurrentUserId() {
        return Optional.empty();
    }
}
