package com.restaurant.ordering.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private Long tableId;
    private List<OrderItemRequest> items;
    
    @Data
    public static class OrderItemRequest {
        private Long menuItemId;
        private int quantity;
    }
} 