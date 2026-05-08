package com.auction.app.domains.user;

import com.auction.app.domains.auction.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Transient
    public UUID getId() {
        return accountNumber;
    }

    @Transient
    public void setId(UUID id) {
        this.accountNumber = id;
    }
}