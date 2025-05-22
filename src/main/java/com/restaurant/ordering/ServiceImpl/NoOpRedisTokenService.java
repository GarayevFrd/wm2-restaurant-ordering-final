package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Service.RedisTokenService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisDisabledCondition;

@Service
@Conditional(RedisDisabledCondition.class)
public class NoOpRedisTokenService implements RedisTokenService {
    
    @Override
    public void whitelistToken(String token) {

    }
    
    @Override
    public void blacklistToken(String token) {

    }
    
    @Override
    public boolean isTokenWhitelisted(String token) {
        return true;
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        return false;
    }
    
    @Override
    public void removeToken(String token) {

    }
} 