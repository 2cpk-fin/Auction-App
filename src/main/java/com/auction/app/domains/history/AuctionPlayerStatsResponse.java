package com.auction.app.domains.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPlayerStatsResponse {

    private UUID userId;
    private Long totalCoinsSpent;
    private Long totalCoinsEarned;
    private Long totalCoinsRefunded;
    private Long totalBidsPlaced;
    private Long auctionsWon;
    private Long auctionsSold;
    private Long auctionsCancelled;
    private Double winRate;
}
