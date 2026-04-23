package com.auction.app.domains.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 16, message = "Password must be at least 6 characters")
    private String password;
}
