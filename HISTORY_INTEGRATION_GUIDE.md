Integration Guide: AuctionHistoryService

This document shows WHERE to add AuctionHistoryService calls in AuctionServiceImpl.
DO NOT modify these methods - just inject AuctionHistoryService and add the calls shown.

==================================================================================

1. AuctionServiceImpl - Inject AuctionHistoryService
==================================================================================

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    // ... other dependencies
    private final AuctionHistoryService auctionHistoryService;  // ADD THIS

    // ... rest of the service
}

==================================================================================

2. In placeBid() method - after successful bid placement
==================================================================================

public void placeBid(UUID auctionId, UUID bidderId, long amount) {
    Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new AuctionNotFoundException(auctionId));

    // ... existing validation logic ...

    // If there's a previous bidder (outbid scenario)
    if (auction.getCurrentBidderId() != null) {
        auctionHistoryService.recordOutbid(
            auction.getCurrentBidderId(),
            auctionId,
            auction.getCurrentBidAmount()
        );
    }

    // Update auction with new bid
    auction.setCurrentBidderId(bidderId);
    auction.setCurrentBidAmount(amount);
    auctionRepository.save(auction);

    // Record the new bid
    auctionHistoryService.recordBidPlaced(bidderId, auctionId, amount);  // ADD THIS
}

==================================================================================

3. In buyInstant() method - after successful purchase
==================================================================================

public void buyInstant(UUID auctionId, UUID buyerId) {
    Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new AuctionNotFoundException(auctionId));

    // ... existing validation logic ...

    auction.setStatus(AuctionStatus.SOLD);
    auction.setCurrentBidderId(buyerId);
    auctionRepository.save(auction);

    // Record history events
    auctionHistoryService.recordAuctionWon(buyerId, auctionId, auction.getBuyoutPrice());  // ADD THIS
    auctionHistoryService.recordAuctionSold(auction.getSellerId(), auctionId, auction.getBuyoutPrice());  // ADD THIS
}

==================================================================================

4. In collectClaim() method - when collecting REFUND claims
==================================================================================

public void collectClaim(UUID claimId, UUID userId) {
    AuctionClaim claim = auctionClaimRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException(claimId));

    if (!claim.getUserId().equals(userId)) {
        throw new UnauthorizedException("Not your claim");
    }

    claim.setCollected(true);
    auctionClaimRepository.save(claim);

    // If this is a refund claim, record it in history
    if (claim.getClaimType() == AuctionClaimType.REFUND) {
        auctionHistoryService.recordRefunded(
            userId,
            claim.getAuctionId(),
            claim.getCoinAmount()
        );  // ADD THIS
    }
}

==================================================================================

5. In cancelAuction() method - after auction cancellation
==================================================================================

public void cancelAuction(UUID auctionId, UUID userId) {
    Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new AuctionNotFoundException(auctionId));

    if (!auction.getSellerId().equals(userId)) {
        throw new UnauthorizedException("Not your auction");
    }

    auction.setStatus(AuctionStatus.CANCELLED);
    auctionRepository.save(auction);

    // Record cancellation
    auctionHistoryService.recordAuctionCancelled(userId, auctionId);  // ADD THIS
}

==================================================================================

Summary of Integration Points:
- placeBid: recordBidPlaced + recordOutbid
- buyInstant: recordAuctionWon + recordAuctionSold
- collectClaim: recordRefunded (conditional on REFUND type)
- cancelAuction: recordAuctionCancelled
