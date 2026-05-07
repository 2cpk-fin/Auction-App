package com.auction.app.domains.auction;

import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_category")
    private String itemCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_type", nullable = false)
    private AuctionType auctionType;

    @Column(name = "starting_bid", nullable = false)
    private Long startingBid;

    @Column(name = "bin_price")
    private Long binPrice;

    @Column(name = "current_bid", nullable = false)
    private Long currentBid;

    @Column(name = "highest_bidder_id")
    private UUID highestBidderId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false)
    private Boolean claimed = false;

    @Version
    private Long version;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auction_bidders", joinColumns = @JoinColumn(name = "auction_id"))
    @Column(name = "bidder_id")
    @Builder.Default
    private Set<UUID> bidderNotifications = new HashSet<>();

}