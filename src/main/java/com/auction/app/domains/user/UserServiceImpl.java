package com.auction.app.domains.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return userMapper.toUserResponse(findUserByEmail(email));
    }

    @Override
    @Transactional
    public UserResponse updateUsername(String email, String newUsername) {
        User user = findUserByEmail(email);
        if (newUsername != null && !newUsername.isBlank()) {
            user.setUsername(newUsername);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateEmail(String email, String newEmail) {
        User user = findUserByEmail(email);

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(newEmail);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfileImage(String email, String imagePath) {
        User user = findUserByEmail(email);
        user.setProfileImagePath(imagePath);
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = findUserByEmail(email);

        // Security: Must verify old password before allowing change
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AccessDeniedException("Existing password does not match");
        }

        if (newPassword != null && newPassword.length() >= 6) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new IllegalArgumentException("New password is too short");
        }
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = findUserByEmail(email);
        userRepository.delete(user);
    }

    // Private Helper
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}