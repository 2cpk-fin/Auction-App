package com.auction.app.domains.auction;

import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    // One user (seller) can host multiple auction sessions (Auction - User)
    @NotNull(message = "Seller reference is required")
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // One auction contains many products (Auction - Product)
    @NotNull(message = "Product reference is required")
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productList;

    // One auction contains many bids (Auction - Bid)
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bidList;

    // Each auction has a start time and end time
    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // The status of the auction session based on the time
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    // Custom
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = (startTime != null) ? startTime.truncatedTo(ChronoUnit.HOURS) : null;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = (endTime != null) ? endTime.truncatedTo(ChronoUnit.HOURS) : null;
    }
}