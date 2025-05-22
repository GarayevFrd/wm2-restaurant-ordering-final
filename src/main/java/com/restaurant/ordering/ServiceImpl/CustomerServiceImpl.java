package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.DTO.OrderItemRequestDTO;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Service.CustomerService;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final TableItemRepository tableItemRepository;

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;


    @Override
    public List<MenuItem> getMenu() {
        return menuItemRepository.findAll();
    }

    @Override
    public Order createOrder(Long tableId, List<OrderItemRequestDTO> items) {
        TableItem table = tableItemRepository.findByTableId(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Order order = Order.builder()
                .customer(table.getCustomer())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .items(createOrderItems(items, table))
                .total(0)
                .build();


        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequestDTO> itemRequests, TableItem table) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDTO itemRequest : itemRequests) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setOrder(table.getOrders().get(0));
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    @Override
    public Order updateOrder(Long orderId, List<OrderItemRequestDTO> items) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.getItems().clear();
        order.getItems().addAll(createOrderItems(items, order.getCustomer().getTable()));

        order.setTotal(calculateTotal(orderId));

        return orderRepository.save(order);
    }


    @Override
    public double calculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getItems().stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"))
                .getStatus();
    }
}
