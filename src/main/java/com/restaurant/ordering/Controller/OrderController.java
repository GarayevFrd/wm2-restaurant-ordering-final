package com.restaurant.ordering.Controller;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @PutMapping("/{orderId}/items")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<OrderDTO> updateOrderItems(
            @PathVariable Long orderId,
            @RequestBody CreateOrderDTO updatedOrder) {
        return ResponseEntity.ok(orderService.updateOrderItems(orderId, updatedOrder));
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<OrderDTO> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, itemId));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/table/{tableId}")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<OrderDTO> getOrderByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getOrderByTable(tableId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/table/{tableId}/history")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<List<OrderDTO>> getOrdersByTableId(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getOrdersByTableId(tableId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('KITCHEN', 'WAITER', 'MANAGER')")
    public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }
}
