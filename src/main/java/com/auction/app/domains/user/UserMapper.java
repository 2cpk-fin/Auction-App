package com.auction.app.domains.user;

import com.auction.app.domains.auth.RegisterRequest;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class UserMapper {

    public User registerRequestToUser(RegisterRequest request) {
        if (request == null) return null;

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .balance(BigDecimal.ZERO)
                .build();
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .profileImagePath(user.getProfileImagePath())
                .build();
    }
}