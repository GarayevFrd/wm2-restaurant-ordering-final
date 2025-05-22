package com.restaurant.ordering.Model.Users;

import com.restaurant.ordering.Enums.UserRole;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Waiter extends User {

    // Custom constructor for username and password
    public Waiter(String username, String passwordHash) {
        super(username, passwordHash);
        this.role = UserRole.WAITER;
    }

}