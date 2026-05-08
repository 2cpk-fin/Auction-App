package com.auction.app.domains.history;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionHistoryService {

    private final AuctionHistoryRepository historyRepository;
    private final AuctionPlayerStatsRepository statsRepository;
    private final AuctionHistoryMapper mapper;

    public void recordBidPlaced(UUID userId, UUID auctionId, long amount) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(userId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.BID_PLACED)
                .coinAmount(amount)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
        updateStatsForBidPlaced(userId);
    }

    public void recordOutbid(UUID previousBidderId, UUID auctionId, long amount) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(previousBidderId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.OUTBID)
                .coinAmount(amount)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
    }

    public void recordAuctionWon(UUID winnerId, UUID auctionId, long amount) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(winnerId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.AUCTION_WON)
                .coinAmount(amount)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
        updateStatsForAuctionWon(winnerId, amount);
    }

    public void recordAuctionSold(UUID sellerId, UUID auctionId, long amount) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(sellerId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.AUCTION_SOLD)
                .coinAmount(amount)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
        updateStatsForAuctionSold(sellerId, amount);
    }

    public void recordRefunded(UUID userId, UUID auctionId, long amount) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(userId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.REFUNDED)
                .coinAmount(amount)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
        updateStatsForRefunded(userId, amount);
    }

    public void recordAuctionCancelled(UUID sellerId, UUID auctionId) {
        AuctionHistoryEvent event = AuctionHistoryEvent.builder()
                .userId(sellerId)
                .auctionId(auctionId)
                .eventType(AuctionHistoryEventType.AUCTION_CANCELLED)
                .coinAmount(null)
                .occurredAt(Instant.now())
                .build();

        historyRepository.save(event);
        updateStatsForAuctionCancelled(sellerId);
    }

    public AuctionPlayerStatsResponse getPlayerStats(UUID userId) {
        AuctionPlayerStats stats = statsRepository.findByUserId(userId)
                .orElse(new AuctionPlayerStats(userId));
        return mapper.toResponse(stats);
    }

    public Page<AuctionHistoryEventResponse> getPlayerHistory(UUID userId, Pageable pageable) {
        return historyRepository.findByUserIdOrderByOccurredAtDesc(userId, pageable)
                .map(mapper::toResponse);
    }

    // Private helper methods for updating stats

    private void updateStatsForBidPlaced(UUID userId) {
        AuctionPlayerStats stats = statsRepository.findByUserId(userId)
                .orElse(new AuctionPlayerStats(userId));
        stats.setTotalBidsPlaced(stats.getTotalBidsPlaced() + 1);
        statsRepository.save(stats);
    }

    private void updateStatsForAuctionWon(UUID winnerId, long amount) {
        AuctionPlayerStats stats = statsRepository.findByUserId(winnerId)
                .orElse(new AuctionPlayerStats(winnerId));
        stats.setAuctionsWon(stats.getAuctionsWon() + 1);
        stats.setTotalCoinsSpent(stats.getTotalCoinsSpent() + amount);
        statsRepository.save(stats);
    }

    private void updateStatsForAuctionSold(UUID sellerId, long amount) {
        AuctionPlayerStats stats = statsRepository.findByUserId(sellerId)
                .orElse(new AuctionPlayerStats(sellerId));
        stats.setAuctionsSold(stats.getAuctionsSold() + 1);
        stats.setTotalCoinsEarned(stats.getTotalCoinsEarned() + amount);
        statsRepository.save(stats);
    }

    private void updateStatsForRefunded(UUID userId, long amount) {
        AuctionPlayerStats stats = statsRepository.findByUserId(userId)
                .orElse(new AuctionPlayerStats(userId));
        stats.setTotalCoinsRefunded(stats.getTotalCoinsRefunded() + amount);
        statsRepository.save(stats);
    }

    private void updateStatsForAuctionCancelled(UUID sellerId) {
        AuctionPlayerStats stats = statsRepository.findByUserId(sellerId)
                .orElse(new AuctionPlayerStats(sellerId));
        stats.setAuctionsCancelled(stats.getAuctionsCancelled() + 1);
        statsRepository.save(stats);
    }
}
