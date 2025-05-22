package com.restaurant.ordering.Controller;

import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Service.WaiterService;
import com.restaurant.ordering.Service.OrderService;
import com.restaurant.ordering.Enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/waiter")
@RequiredArgsConstructor
public class WaiterController {

    private final WaiterService waiterService;
    private final OrderService orderService;


    @GetMapping("/ready-orders")
    public ResponseEntity<List<OrderDTO>> getReadyOrders() {
        List<OrderDTO> readyOrders = orderService.getOrdersByStatus(OrderStatus.READY);
        if (readyOrders.isEmpty()) {
            throw new NoSuchElementException("There are no ready orders to deliver.");
        }
        return ResponseEntity.ok(readyOrders);
    }


    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<String> markOrderDelivered(@PathVariable Long orderId) {
        try {
            waiterService.markOrderDelivered(orderId);
            return ResponseEntity.ok("Order marked as DELIVERED");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Order with ID " + orderId + " not found.");
        }
    }
}