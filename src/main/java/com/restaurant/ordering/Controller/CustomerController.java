package com.restaurant.ordering.Controller;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Service.OrderService;
import com.restaurant.ordering.Service.MenuService;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableItemRepository tableItemRepository;

    @PostMapping("/order")
    public OrderDTO createOrder(@RequestBody CreateOrderDTO order) {
        return orderService.createOrder(order);
    }


    @GetMapping("/menu/{tableId}")
    public List<MenuItem> getMenu(@PathVariable String tableId) {
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        if (menuItems.isEmpty()) {
            throw new NoSuchElementException("Menu is currently unavailable.");
        }
        return menuService.getAllMenuItems();
    }

    @PutMapping("/order/{orderId}/item")
    public OrderDTO updateOrderItem(@PathVariable Long orderId, @RequestBody CreateOrderDTO updatedOrder) {
        if (updatedOrder.getItems() == null || updatedOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("Updated order must contain at least one item.");
        }

        return orderService.updateOrderItems(orderId, updatedOrder);
    }

    @DeleteMapping("/order/{orderId}/item/{itemId}")
    public OrderDTO removeOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.removeItemFromOrder(orderId, itemId);
    }

    @GetMapping("/order/status/{orderId}")
    public OrderStatus getOrderStatus(@PathVariable Long orderId) {
        return orderService.getOrderStatus(orderId);
    }

    @GetMapping("/order/{orderId}")
    public OrderDTO getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}
