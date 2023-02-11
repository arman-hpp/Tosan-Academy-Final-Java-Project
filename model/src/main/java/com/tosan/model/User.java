package com.tosan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User extends BaseEntity implements UserDetails {
    private static final int PASSWORD_EXPIRATION_TIME = 30; // 30 days
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION = 24; // 24 hours

    @Column(name = "username", length = 40, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 256, nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserTypes userType;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_password_changed_date")
    private LocalDateTime lastPasswordChangedDate;

    @Column(name = "user_state")
    @Enumerated(EnumType.ORDINAL)
    private UserState userState;

    @Column(name = "failed_attempt")
    private Integer failedAttempt;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Override
    public String toString() {
        return getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(getUserType().toString()));

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return getUserState() != UserState.Expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getUserState() != UserState.Locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (getLastPasswordChangedDate() == null) {
            return true;
        }

        var currentDate = LocalDateTime.now();
        var lastChangedDate = getLastPasswordChangedDate();

        return currentDate.isBefore(lastChangedDate.plusDays(PASSWORD_EXPIRATION_TIME));
    }

    @Override
    public boolean isEnabled() {
        return getUserState() == UserState.Enabled;
    }

    public void unlock() {
        setFailedAttempt(0);
        setLockTime(null);
        setUserState(UserState.Enabled);
    }

    public void lock() {
        setLockTime(LocalDateTime.now());
        setUserState(UserState.Locked);
    }

    public void increaseFailedAttempts() {
        setFailedAttempt(getFailedAttempt() + 1);
    }

    public boolean isFailedAttemptsExceeded() {
        return getFailedAttempt() > User.MAX_FAILED_ATTEMPTS - 1;
    }

    public boolean isLockTimeFinished() {
        return getLockTime().plusHours(LOCK_TIME_DURATION).isAfter(LocalDateTime.now());
    }
}
