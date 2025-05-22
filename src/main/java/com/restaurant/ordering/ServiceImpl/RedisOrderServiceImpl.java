package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Service.RedisOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisEnabledCondition;
import java.util.concurrent.TimeUnit;

@Service
@Conditional(RedisEnabledCondition.class)
public class RedisOrderServiceImpl implements RedisOrderService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ORDER_STATUS_PREFIX = "order:status:";
    private static final String ORDER_SESSION_PREFIX = "order:session:";
    private static final long ORDER_SESSION_TIMEOUT = 24; // hours
    
    public RedisOrderServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void saveOrderStatus(Long orderId, OrderStatus status) {
        String key = ORDER_STATUS_PREFIX + orderId;
        redisTemplate.opsForValue().set(key, status.toString());
    }
    
    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        String key = ORDER_STATUS_PREFIX + orderId;
        String status = (String) redisTemplate.opsForValue().get(key);
        return status != null ? OrderStatus.valueOf(status) : null;
    }
    
    @Override
    public void saveOrderSession(Long tableId, Long orderId) {
        String key = ORDER_SESSION_PREFIX + tableId;
        redisTemplate.opsForValue().set(key, orderId.toString(), ORDER_SESSION_TIMEOUT, TimeUnit.HOURS);
    }
    
    @Override
    public Long getOrderSession(Long tableId) {
        String key = ORDER_SESSION_PREFIX + tableId;
        String orderId = (String) redisTemplate.opsForValue().get(key);
        return orderId != null ? Long.parseLong(orderId) : null;
    }
    
    @Override
    public void removeOrderSession(Long tableId) {
        String key = ORDER_SESSION_PREFIX + tableId;
        redisTemplate.delete(key);
    }
} 