package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Service.WaiterService;
import com.restaurant.ordering.Service.OrderService;
import com.restaurant.ordering.ServiceImpl.OrderMessageProducer;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import com.restaurant.ordering.Enums.OrderStatus;

@Service
@RequiredArgsConstructor
public class WaiterServiceImpl implements WaiterService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderMessageProducer orderMessageProducer;

    @Override
    public List<Order> getReadyOrders() {
        return orderRepository.findByStatus(OrderStatus.READY);
    }

    @Override
    @Transactional
    public void markOrderDelivered(Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED);
        } catch (Exception e) {
            throw new NoSuchElementException("Failed to mark order as delivered: " + e.getMessage());
        }
    }
}
