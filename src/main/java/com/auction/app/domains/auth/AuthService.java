package com.auction.app.domains.auth;

import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserMapper;
import com.auction.app.domains.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Check if the email is already existed
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user here
        User user = userMapper.registerRequestToUser(registerRequest); // Map the Request to the Entity
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Hash the password
        userRepository.save(user); // Save to the DB

        // Return the Response
        return AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token("dummy_token")
                .build();
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        // Check the email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check the password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Return the Response
        return AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token("dummy_token")
                .build();
    }
}
