package com.auction.app.infrastructure.session;

import com.auction.app.domains.auth.AuthResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("singleton")
@Getter
@Setter
public class UserSession {
    private AuthResponse currentUser;
    private String token;

    public void setAuthResponse(AuthResponse authResponse) {
        this.currentUser = authResponse;
        this.token = authResponse.getToken();
    }

    public UUID getCurrentUserId() {
        // Extract UUID from token or another identifier
        // For now, using a simple approach - you may need to decode the JWT
        if (currentUser != null) {
            // This assumes the token contains the UUID or there's another way to get it
            // For demo purposes, generating a UUID from email hash
            return UUID.nameUUIDFromBytes(currentUser.getEmail().getBytes());
        }
        return null;
    }

    public void clear() {
        this.currentUser = null;
        this.token = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null && token != null;
    }
}
