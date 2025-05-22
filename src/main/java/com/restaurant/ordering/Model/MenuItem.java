package com.restaurant.ordering.Model;

import com.restaurant.ordering.Enums.MenuCategory;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double price;

    @Enumerated(EnumType.STRING)
    private MenuCategory category; // Enum for APPETIZER, MAIN_COURSE, etc.
}
