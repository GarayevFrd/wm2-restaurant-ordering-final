package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Repository.UserRepository;
import com.restaurant.ordering.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPasswordHash(), List.of(authority)
        );
    }

    @Override
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }


        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));


        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {

        throw new UnsupportedOperationException("Login is handled by Spring Security");
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
