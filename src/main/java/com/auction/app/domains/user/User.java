package com.auction.app.domains.user;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import com.auction.app.domains.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @UuidGenerator
    @Column(name = "public_user_id", nullable = false, updatable = false)
    private UUID accountNumber;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @CreatedDate
    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant createAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productList;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auctionList;

    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bidList;

    // Transactions where this user was the one selling
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Transaction> sales;

    // Transactions where this user was the one bidding/buying
    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL)
    private List<Transaction> purchases;
}