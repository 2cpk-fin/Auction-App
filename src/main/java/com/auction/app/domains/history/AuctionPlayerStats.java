package com.auction.app.domains.history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "auction_player_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPlayerStats {

    @Id
    private UUID userId;

    private Long totalCoinsSpent;
    private Long totalCoinsEarned;
    private Long totalCoinsRefunded;
    private Long totalBidsPlaced;
    private Long auctionsWon;
    private Long auctionsSold;
    private Long auctionsCancelled;

    public AuctionPlayerStats(UUID userId) {
        this.userId = userId;
        this.totalCoinsSpent = 0L;
        this.totalCoinsEarned = 0L;
        this.totalCoinsRefunded = 0L;
        this.totalBidsPlaced = 0L;
        this.auctionsWon = 0L;
        this.auctionsSold = 0L;
        this.auctionsCancelled = 0L;
    }

    public double getWinRate() {
        if (totalBidsPlaced == 0) {
            return 0.0;
        }
        return (double) auctionsWon / totalBidsPlaced;
    }
}
