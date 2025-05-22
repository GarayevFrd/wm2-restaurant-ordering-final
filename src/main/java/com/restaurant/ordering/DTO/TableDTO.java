package com.restaurant.ordering.DTO;

import lombok.Data;

@Data
public class TableDTO {
    private Long id;
    private int tableNumber;
    private int capacity;
    private boolean occupied;
} 