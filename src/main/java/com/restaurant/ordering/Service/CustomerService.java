package com.restaurant.ordering.Service;

import com.restaurant.ordering.DTO.OrderItemRequestDTO;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Enums.OrderStatus;

import java.util.*;

public interface CustomerService {

    List<MenuItem> getMenu();
    Order createOrder(Long tableId, List<OrderItemRequestDTO> items);
    Order updateOrder(Long orderId, List<OrderItemRequestDTO> items);
    double calculateTotal(Long orderId);
    OrderStatus getOrderStatus(Long orderId);
}