package com.restaurant.ordering.Service;

import com.restaurant.ordering.Enums.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisEnabledCondition;
import java.util.concurrent.TimeUnit;

public interface RedisOrderService {
    void saveOrderStatus(Long orderId, OrderStatus status);
    OrderStatus getOrderStatus(Long orderId);
    void saveOrderSession(Long tableId, Long orderId);
    Long getOrderSession(Long tableId);
    void removeOrderSession(Long tableId);
} 