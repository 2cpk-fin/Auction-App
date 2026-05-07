package com.auction.app.domains.auction;

import com.auction.app.domains.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionResponse {
    private UUID id;
    private UUID sellerId;
    private String sellerName;
    private String itemName;
    private String itemCategory;
    private AuctionType auctionType;
    private Long startingBid;
    private Long binPrice;
    private Long currentBid;
    private UUID highestBidderId;
    private Instant startTime;
    private Instant endTime;
    private AuctionStatus status;
    private Boolean claimed;
    private Long bidCount;
}