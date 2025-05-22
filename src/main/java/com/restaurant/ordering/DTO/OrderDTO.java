package com.restaurant.ordering.DTO;

import com.restaurant.ordering.Enums.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long tableId;
    private List<OrderItemDTO> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private double totalAmount;
    
    @Data
    public static class OrderItemDTO {
        private Long menuItemId;
        private String itemName;
        private int quantity;
        private double price;
    }
} 