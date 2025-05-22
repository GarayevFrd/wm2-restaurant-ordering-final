package com.restaurant.ordering.Service;

import com.restaurant.ordering.Model.Order;

import java.util.*;

public interface KitchenStaffService {
    List<Order> getIncomingOrders();
    void markOrderInPreparation(Long orderId);
    void markOrderReady(Long orderId);
    List<Order> getOrdersInPreparation();
    List<Order> getReadyOrders();
}
