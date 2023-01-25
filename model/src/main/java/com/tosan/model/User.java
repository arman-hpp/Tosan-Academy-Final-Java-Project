package com.tosan.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User extends BaseEntity {
    @Column(name = "username", length = 40, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 256, nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserTypes userType;
}
