package com.auction.app.domains.user;

import com.auction.app.domains.auth.RegisterRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserMapper {
    public User registerRequestToUser(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setBalance(BigDecimal.valueOf(0.0)); // Initialize new users with 0 balance

        return user;
    }

    public UserResponse userToResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setPublicUserId(user.getAccountNumber());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    public void updateUserFromRequest(UserRequest request, User user) {
        if (request == null || user == null) return;

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
    }
}