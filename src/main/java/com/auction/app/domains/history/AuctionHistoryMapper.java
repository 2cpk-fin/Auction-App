package com.auction.app.domains.history;

import org.springframework.stereotype.Component;

@Component
public class AuctionHistoryMapper {

    public AuctionHistoryEventResponse toResponse(AuctionHistoryEvent event) {
        if (event == null) {
            return null;
        }

        return AuctionHistoryEventResponse.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .auctionId(event.getAuctionId())
                .eventType(event.getEventType())
                .coinAmount(event.getCoinAmount())
                .occurredAt(event.getOccurredAt())
                .build();
    }

    public AuctionPlayerStatsResponse toResponse(AuctionPlayerStats stats) {
        if (stats == null) {
            return null;
        }

        return AuctionPlayerStatsResponse.builder()
                .userId(stats.getUserId())
                .totalCoinsSpent(stats.getTotalCoinsSpent())
                .totalCoinsEarned(stats.getTotalCoinsEarned())
                .totalCoinsRefunded(stats.getTotalCoinsRefunded())
                .totalBidsPlaced(stats.getTotalBidsPlaced())
                .auctionsWon(stats.getAuctionsWon())
                .auctionsSold(stats.getAuctionsSold())
                .auctionsCancelled(stats.getAuctionsCancelled())
                .winRate(stats.getWinRate())
                .build();
    }
}
