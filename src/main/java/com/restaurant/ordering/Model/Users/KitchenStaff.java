package com.restaurant.ordering.Model.Users;

import com.restaurant.ordering.Enums.UserRole;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class KitchenStaff extends User {

    // Custom constructor for username and password
    public KitchenStaff(String username, String passwordHash) {
        super(username, passwordHash);
        this.role = UserRole.KITCHEN;
    }

}
