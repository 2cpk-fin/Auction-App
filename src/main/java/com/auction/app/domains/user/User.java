package com.auction.app.domains.user;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import com.auction.app.domains.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(
            name = "public_user_id",
            nullable = false,
            updatable = false,
            columnDefinition = "UUID DEFAULT gen_random_uuid()"
    )
    private UUID accountNumber;

    @Column(name = "username", nullable = false)
    @Size(max = 255, message = "Username must not exceed 255 characters")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 6, max = 25, message = "Invalid password length")
    private String password;

    @Column(name = "balance", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Balance must be positive")
    private BigDecimal balance;

    @Column(
            name = "create_at",
            nullable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant createAt;

    // One user can create many products (User - Product)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productList;

    // One user can host many auction sessions (Auction - User)
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auctionList;

    // One user can bid many times in one auction (User - Bid)
    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bidList;

    // Transactions where this user was the one selling
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Transaction> sales;

    // Transactions where this user was the one bidding/buying
    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL)
    private List<Transaction> purchases;
}
