package com.restaurant.ordering.Service;

public interface RedisTokenService {
    void whitelistToken(String token);
    void blacklistToken(String token);
    boolean isTokenWhitelisted(String token);
    boolean isTokenBlacklisted(String token);
    void removeToken(String token);
} 