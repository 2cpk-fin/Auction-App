package com.auction.app.domains.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "public_user_id", nullable = false, updatable = false)
    @ColumnDefault("gen_random_uuid()")
    private UUID publicUserId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance", precision = 15, scale = 2)
    @Check(constraints = "balance >= 0")
    @DecimalMin(value = "0.0", message = "Balance must be positive")
    private java.math.BigDecimal balance;

    @Column(name = "create_at", nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant createAt;

    @Column(name = "profile_picture")
    @URL
    private String profilePicture;
}
