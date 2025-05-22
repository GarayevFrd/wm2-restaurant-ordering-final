package com.restaurant.ordering.Model.Users;

import com.restaurant.ordering.Enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Entity;

@Entity
@Getter
@Setter // prevent public mutation
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    protected UserRole role;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}

