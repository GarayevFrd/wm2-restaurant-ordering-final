package com.restaurant.ordering.Controller;

import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Service.KitchenStaffService;
import com.restaurant.ordering.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenStaffService kitchenStaffService;
    private final OrderService orderService;

    @GetMapping("/incoming")
    @PreAuthorize("hasRole('KITCHEN')")
    public ResponseEntity<List<OrderDTO>> getIncomingOrders() {
        List<OrderDTO> orders = orderService.getOrdersByStatus(OrderStatus.CREATED);
        if (orders.isEmpty()) {
            throw new NoSuchElementException("No incoming orders found.");
        }
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/prepare")
    @PreAuthorize("hasRole('KITCHEN')")
    public ResponseEntity<String> markInPreparation(@PathVariable Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, OrderStatus.IN_PREPARATION);
            return ResponseEntity.ok("Order marked as IN_PREPARATION");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Order with ID " + orderId + " not found.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Order cannot be marked as IN_PREPARATION: " + e.getMessage());
        }
    }


    @PutMapping("/{orderId}/ready")
    @PreAuthorize("hasRole('KITCHEN')")
    public ResponseEntity<String> markReady(@PathVariable Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, OrderStatus.READY);
            return ResponseEntity.ok("Order marked as READY");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Order with ID " + orderId + " not found.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Order cannot be marked as READY: " + e.getMessage());
        }
    }


    @GetMapping("/preparing")
    @PreAuthorize("hasRole('KITCHEN')")
    public ResponseEntity<List<OrderDTO>> getOrdersInPreparation() {
        List<OrderDTO> preparingOrders = orderService.getOrdersByStatus(OrderStatus.IN_PREPARATION);
        if (preparingOrders.isEmpty()) {
            throw new NoSuchElementException("No orders currently in preparation.");
        }
        return ResponseEntity.ok(preparingOrders);
    }


    @GetMapping("/ready")
    @PreAuthorize("hasRole('KITCHEN')")
    public ResponseEntity<List<OrderDTO>> getReadyOrders() {
        List<OrderDTO> readyOrders = orderService.getOrdersByStatus(OrderStatus.READY);
        if (readyOrders.isEmpty()) {
            throw new NoSuchElementException("No orders are currently ready.");
        }
        return ResponseEntity.ok(readyOrders);
    }
}
