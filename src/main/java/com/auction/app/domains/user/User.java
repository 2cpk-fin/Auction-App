package com.auction.app.domains.user;

import com.auction.app.domains.auction.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "profile_image_path")
    private String profileImagePath; // Local filename or relative path

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Auction> auctions = new ArrayList<>();

    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();
}