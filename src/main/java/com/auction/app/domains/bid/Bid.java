package com.auction.app.domains.bid;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.product.Product;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    // It's possible to have many bids inside the auction (Auction - Bid)
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    // One bidder can bid many times (User - Bid)
    @NotNull(message = "Bidder reference is required")
    @ManyToOne
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    // Each bid can only have one product (Product - Bid)
    @NotNull(message = "Product reference is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    // Bid price's here to track money change
    @NotNull(message = "Bid price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Bid price must be strictly greater than 0")
    @Column(name = "bid_price", nullable = false)
    private BigDecimal bidPrice;

    // Store the bid time
    @NotNull(message = "Bid timestamp is required")
    @Column(
            name = "bid_at",
            nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime bidAt;

    // One bid have one transaction (Bid - Transaction)
    @OneToOne(mappedBy = "bid", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bid bid;
}