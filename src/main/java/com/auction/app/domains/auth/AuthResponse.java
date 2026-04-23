package com.auction.app.domains.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String token;

    private String username;

    private String email;
}
