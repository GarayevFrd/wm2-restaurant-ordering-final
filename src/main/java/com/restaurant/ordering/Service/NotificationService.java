package com.restaurant.ordering.Service;

import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    

    public SseEmitter createEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Long timeout
        
        // Remove emitter on completion, timeout, or error
        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError(e -> {
            logger.error("SSE error for user {}: {}", userId, e.getMessage());
            removeEmitter(userId);
        });
        
        // Store the emitter
        emitters.put(userId, emitter);
        logger.info("Created SSE emitter for user: {}", userId);
        
        // Send initial connection established event
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECT")
                    .data("Connected to notification service"));
        } catch (IOException e) {
            logger.error("Error sending initial event to user {}: {}", userId, e.getMessage());
            removeEmitter(userId);
        }
        
        return emitter;
    }
    
    /**
     * Removes an SSE emitter for a user
     * @param userId The user ID
     */
    public void removeEmitter(String userId) {
        emitters.remove(userId);
        logger.info("Removed SSE emitter for user: {}", userId);
    }
    
    /**
     * Sends an order status notification to a specific user
     * @param userId The user ID
     * @param order The order with updated status
     */
    public void sendOrderStatusNotification(String userId, OrderDTO order) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("ORDER_STATUS_CHANGED")
                        .data(order));
                logger.info("Sent order status notification to user {}: Order #{} status changed to {}", 
                        userId, order.getId(), order.getStatus());
            } catch (IOException e) {
                logger.error("Error sending notification to user {}: {}", userId, e.getMessage());
                removeEmitter(userId);
            }
        }
    }
    
    /**
     * Broadcasts an order status notification to all connected users
     * @param order The order with updated status
     */
    public void broadcastOrderStatusChange(OrderDTO order) {
        logger.info("Broadcasting order status change: Order #{} status changed to {}", 
                order.getId(), order.getStatus());
        
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ORDER_STATUS_CHANGED")
                        .data(order));
            } catch (IOException e) {
                logger.error("Error broadcasting to user {}: {}", userId, e.getMessage());
                removeEmitter(userId);
            }
        });
    }
    
    /**
     * Gets the count of active SSE connections
     * @return The number of active connections
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }
}