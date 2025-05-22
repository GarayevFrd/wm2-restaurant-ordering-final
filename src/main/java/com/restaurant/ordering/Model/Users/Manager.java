package com.restaurant.ordering.Model.Users;

import com.restaurant.ordering.Enums.UserRole;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Manager extends User {

    public Manager(String username, String passwordHash) {
        super(username, passwordHash);
        this.role = UserRole.MANAGER;
    }
}