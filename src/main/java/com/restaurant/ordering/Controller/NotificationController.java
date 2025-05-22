package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String userId = auth.getName();
            return notificationService.createEmitter(userId);
        } else {
            throw new SecurityException("User must be authenticated to subscribe to notifications");
        }
    }


    @GetMapping(value = "/customer/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeCustomer() {
        // For customer notifications, we use a session ID or a random ID
        // In a real application, you might want to use a more secure approach
        String sessionId = generateSessionId();
        return notificationService.createEmitter(sessionId);
    }


    private String generateSessionId() {
        return "customer-" + System.currentTimeMillis();
    }
}