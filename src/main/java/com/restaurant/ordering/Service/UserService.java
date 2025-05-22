package com.restaurant.ordering.Service;

import com.restaurant.ordering.Model.Users.User;

import java.util.*;


public interface UserService {
    User register(User user);
    User login(String username, String password);
    Optional<User> findById(Long id);
}