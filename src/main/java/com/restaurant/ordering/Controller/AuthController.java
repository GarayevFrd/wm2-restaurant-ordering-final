package com.restaurant.ordering.Controller;

import com.restaurant.ordering.DTO.LoginResponseDTO;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.Users.KitchenStaff;
import com.restaurant.ordering.Model.Users.Manager;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Model.Users.Waiter;
import com.restaurant.ordering.ServiceImpl.UserServiceImpl;
import com.restaurant.ordering.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String role = authentication.getAuthorities().iterator().next().getAuthority(); // get the single role
        String token = jwtTokenProvider.createToken(username, role);
        
        LoginResponseDTO response = new LoginResponseDTO(token, username, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String roleStr = payload.get("role");

        if (username == null || password == null || roleStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, password, and role are required"));
        }

        try {
            UserRole role = UserRole.valueOf(roleStr.toUpperCase());
            User user;


            switch (role) {
                case MANAGER:
                    user = new Manager(username, password);
                    break;
                case WAITER:
                    user = new Waiter(username, password);
                    break;
                case KITCHEN:
                    user = new KitchenStaff(username, password);
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
            }


            User registeredUser = userService.register(user);

            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "username", registeredUser.getUsername(),
                "role", registeredUser.getRole().name()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred during registration"));
        }
    }
}
