package com.restaurant.ordering.Service;

import com.restaurant.ordering.Model.Order;

import java.util.*;

public interface WaiterService {
    List<Order> getReadyOrders();
    void markOrderDelivered(Long orderId);
}