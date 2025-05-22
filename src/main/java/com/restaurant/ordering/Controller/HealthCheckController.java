package com.restaurant.ordering.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public HealthCheckController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        

        boolean redisConnected = checkRedisConnection();
        response.put("redis", redisConnected ? "Connected" : "Not connected");
        
        return ResponseEntity.ok(response);
    }
    
    private boolean checkRedisConnection() {
        try {
            // Try to ping Redis
            String pingResult = redisTemplate.getConnectionFactory().getConnection().ping();
            return pingResult != null && pingResult.equalsIgnoreCase("PONG");
        } catch (Exception e) {
            return false;
        }
    }
}