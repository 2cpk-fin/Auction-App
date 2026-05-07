package com.auction.app.domains.auction;

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
public class AuctionClaimResponse {
    private UUID id;
    private UUID userId;
    private UUID auctionId;
    private String itemName;
    private Long coinAmount;
    private AuctionClaimType claimType;
    private Boolean claimed;
    private Instant createdAt;
}
