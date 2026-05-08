package com.auction.app.infrastructure.scheduler;

import com.auction.app.domains.auction.auction.Auction;
import com.auction.app.domains.auction.auction.AuctionRepository;
import com.auction.app.domains.auction.auction.AuctionStatus;
import com.auction.app.domains.auction.auctionClaim.AuctionClaim;
import com.auction.app.domains.auction.auctionClaim.AuctionClaimRepository;
import com.auction.app.domains.auction.auctionClaim.AuctionClaimType;
import com.auction.app.domains.bid.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AuctionExpiryScheduler {

    private final AuctionRepository auctionRepository;
    private final AuctionClaimRepository auctionClaimRepository;
    private final BidRepository bidRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    @Transactional
    public void processExpiredAuctions() {
        log.info("Processing expired auctions...");

        List<Auction> expiredAuctions = auctionRepository.findExpiredAuctions(Instant.now());

        for (Auction auction : expiredAuctions) {
            try {
                if (auction.getHighestBidderId() != null) {
                    // Auction has bids - winner claims item, seller claims coins
                    auction.setStatus(AuctionStatus.SOLD);

                    AuctionClaim itemClaim = AuctionClaim.builder()
                            .user(auction.getSeller())
                            .auction(auction)
                            .claimType(AuctionClaimType.ITEM)
                            .claimed(false)
                            .build();
                    auctionClaimRepository.save(itemClaim);

                    AuctionClaim coinsClaim = AuctionClaim.builder()
                            .user(auction.getSeller())
                            .auction(auction)
                            .claimType(AuctionClaimType.COINS)
                            .claimed(false)
                            .build();
                    auctionClaimRepository.save(coinsClaim);

                    log.info("Auction {} SOLD to bidder {}", auction.getId(), auction.getHighestBidderId());
                } else {
                    // No bids - auction expired
                    auction.setStatus(AuctionStatus.EXPIRED);

                    AuctionClaim returnClaim = AuctionClaim.builder()
                            .user(auction.getSeller())
                            .auction(auction)
                            .claimType(AuctionClaimType.ITEM)
                            .claimed(false)
                            .build();
                    auctionClaimRepository.save(returnClaim);

                    log.info("Auction {} EXPIRED with no bids", auction.getId());
                }

                auctionRepository.save(auction);

                // Broadcast result via WebSocket
                broadcastAuctionResult(auction);
            } catch (Exception e) {
                log.error("Error processing expired auction {}: {}", auction.getId(), e.getMessage(), e);
            }
        }

        log.info("Finished processing expired auctions. Processed {} auctions", expiredAuctions.size());
    }

    private void broadcastAuctionResult(Auction auction) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("auctionId", auction.getId());
        payload.put("status", auction.getStatus());
        payload.put("endTime", auction.getEndTime());
        payload.put("highestBidderId", auction.getHighestBidderId());

        String destination = "/topic/auction/" + auction.getId();
        messagingTemplate.convertAndSend(destination, (Object) payload);
    }
}
