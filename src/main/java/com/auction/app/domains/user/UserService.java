package com.auction.app.domains.user;

import java.util.UUID;

public interface UserService {
    UserResponse getUserByEmail(String email);

    UserResponse updateUsername(String email, String newUsername);

    UserResponse updateEmail(String email, String newEmail);

    UserResponse updateProfileImage(String email, String imagePath);

    void updatePassword(String email, String oldPassword, String newPassword);

    void deleteUserByEmail(String email);
}