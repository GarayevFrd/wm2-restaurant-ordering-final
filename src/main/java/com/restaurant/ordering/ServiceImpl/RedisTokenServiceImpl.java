package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Service.RedisTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Conditional;
import com.restaurant.ordering.Config.RedisEnabledCondition;
import java.util.concurrent.TimeUnit;

@Service
@Conditional(RedisEnabledCondition.class)
public class RedisTokenServiceImpl implements RedisTokenService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_WHITELIST_PREFIX = "token:whitelist:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final long TOKEN_EXPIRATION = 24; // hours
    
    public RedisTokenServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void whitelistToken(String token) {
        String key = TOKEN_WHITELIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "valid", TOKEN_EXPIRATION, TimeUnit.HOURS);
    }
    
    @Override
    public void blacklistToken(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "invalid", TOKEN_EXPIRATION, TimeUnit.HOURS);

        redisTemplate.delete(TOKEN_WHITELIST_PREFIX + token);
    }
    
    @Override
    public boolean isTokenWhitelisted(String token) {
        String key = TOKEN_WHITELIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    @Override
    public void removeToken(String token) {
        redisTemplate.delete(TOKEN_WHITELIST_PREFIX + token);
        redisTemplate.delete(TOKEN_BLACKLIST_PREFIX + token);
    }
} 