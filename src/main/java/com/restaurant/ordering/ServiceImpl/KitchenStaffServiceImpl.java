package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Service.KitchenStaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenStaffServiceImpl implements KitchenStaffService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getIncomingOrders() {
        return orderRepository.findByStatus(OrderStatus.CREATED);
    }

    @Override
    @Transactional
    public void markOrderInPreparation(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(OrderStatus.IN_PREPARATION);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void markOrderReady(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersInPreparation() {
        return orderRepository.findByStatus(OrderStatus.IN_PREPARATION);
    }

    @Override
    public List<Order> getReadyOrders() {
        return orderRepository.findByStatus(OrderStatus.READY);
    }
}
