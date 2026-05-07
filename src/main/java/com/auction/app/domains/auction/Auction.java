package com.auction.app.domains.auction;

import com.auction.app.domains.auction.auctionItem.AuctionItem;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auctions")
public class Auction {

    /*
    Required information to create new auction
    Also, this is the basic stat
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionItem> auctionItems;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    // The registration table
    @ElementCollection
    @CollectionTable(name = "auction_registrations", joinColumns = @JoinColumn(name = "auction_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "is_registered")
    @Builder.Default
    private Map<Long, Boolean> registeredUsers = new HashMap<>();

    // One auction contains many bids (Auction - Bid)
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bid> bidList = new ArrayList<>();

}