package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Service.RedisOrderService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisDisabledCondition;

@Service
@Conditional(RedisDisabledCondition.class)
public class NoOpRedisOrderService implements RedisOrderService {
    
    @Override
    public void saveOrderStatus(Long orderId, OrderStatus status) {

    }
    
    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        return null;
    }
    
    @Override
    public void saveOrderSession(Long tableId, Long orderId) {

    }
    
    @Override
    public Long getOrderSession(Long tableId) {
        return null;
    }
    
    @Override
    public void removeOrderSession(Long tableId) {

    }
} 