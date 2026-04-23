package com.auction.app.domains.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserById(Long userId) {
        return userMapper.userToResponse(findUserById(userId));
    }

    @Transactional
    public UserResponse updateUsername(Long userId, String newUsername) {
        User user = findUserById(userId);
        if (newUsername != null && !newUsername.isBlank()) {
            user.setUsername(newUsername);
        }
        return userMapper.userToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateEmail(Long userId, String newEmail) {
        User user = findUserById(userId);
        if (newEmail != null && !newEmail.isBlank()) {
            // In a real app, check if email is already in use here
            user.setEmail(newEmail);
        }
        return userMapper.userToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateProfilePicture(Long userId, String newUrl) {
        User user = findUserById(userId);
        user.setProfilePicture(newUrl); // URL can be null to remove picture
        return userMapper.userToResponse(userRepository.save(user));
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = findUserById(userId);

        // Check for the old password for verification
        if (!passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        // Update new password
        if (newPassword != null && newPassword.length() >= 6) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    // Helper
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}