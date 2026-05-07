package com.auction.app.domains.bid;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.auction.auctionItem.AuctionItem;
import com.auction.app.domains.transaction.Transaction;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bids")
public class Bid {

    /*
        Required information for bid
    */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_item_id", nullable = false)
    private AuctionItem auctionItem;

    @Column(name = "bid_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal bidPrice;

    @CreatedDate
    @Column(name = "bid_at", nullable = false)
    private Instant bidAt;

    // This is for after bid
    @OneToOne(mappedBy = "bid", cascade = CascadeType.ALL, orphanRemoval = true)
    private Transaction transaction;
}