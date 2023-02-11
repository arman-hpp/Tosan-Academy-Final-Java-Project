package com.tosan.core_banking.services;

import com.tosan.core_banking.dtos.UserDto;
import com.tosan.model.User;
import com.tosan.model.UserTypes;
import com.tosan.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Service
public class AuthenticationService implements UserDetailsService {
    private final UserRepository _userRepository;
    private final ModelMapper _modelMapper;

    public AuthenticationService(UserRepository userRepository, ModelMapper modelMapper) {
        _userRepository = userRepository;
        _modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = _userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("The user cannot be found");
        }

        return user;
    }

    public void logFailedLogin(String username) {
        try {
            var user = _userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return;
            }

            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.isFailedAttemptsExceeded()) {
                    user.lock();
                } else {
                    user.increaseFailedAttempts();
                }
            } else if (!user.isAccountNonLocked()) {
                if (user.isLockTimeFinished()) {
                    user.unlock();
                }
            }

            _userRepository.save(user);
        } catch (Exception ex) {
            // ignore
        }
    }

    public void logSuccessfulLogin(String username) {
        try {
            var user = _userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return;
            }

            user.setFailedAttempt(0);
            user.setLastLoginDate(LocalDateTime.now());

            _userRepository.save(user);
        } catch (Exception ex) {
            // ignore
        }
    }

    public Boolean isUserAuthenticated() {
        var context = SecurityContextHolder.getContext();
        if(context == null)
            return false;

        var authentication = context.getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.isAuthenticated();
    }

    public Boolean isUserAdmin() {
        var user = loadCurrentUser();
        if(user == null){
            return false;
        } else {
            return user.getUserType() == UserTypes.ROLE_ADMIN;
        }
    }

    public UserDto loadCurrentUser() {
        var context = SecurityContextHolder.getContext();
        if(context == null)
            return null;

        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        if(principal instanceof User user){
            return _modelMapper.map(user, UserDto.class);
        }

        return new UserDto();
    }

    public Optional<String> loadCurrentUsername() {
        var user = loadCurrentUser();
        if(user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getUsername());
    }

    public Optional<Long> loadCurrentUserId() {
        var user = loadCurrentUser();
        if(user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getId());
    }
}
