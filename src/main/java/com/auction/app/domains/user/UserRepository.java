package com.auction.app.domains.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotBlank(message = "Email cannot be empty") @Email String email);

    Optional<User> findByAccountNumber(UUID accountNumber);
}
