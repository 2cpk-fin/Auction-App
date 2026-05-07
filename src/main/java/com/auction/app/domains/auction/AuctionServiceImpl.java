package com.auction.app.domains.auction;

import com.auction.app.domains.auction.exceptions.*;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.bid.BidMapper;
import com.auction.app.domains.bid.BidRepository;
import com.auction.app.domains.bid.BidResponse;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final AuctionClaimRepository claimRepository;
    private final AuctionClaimMapper claimMapper;
    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final UserRepository userRepository;

    @Value("${auction.max-active-per-player:3}")
    private int maxActiveAuctions;

    @Value("${auction.min-bid-raise:1}")
    private long minimumBidRaise;

    @Override
    public AuctionResponse createAuction(AuctionRequest request, UUID sellerId) {
        User seller = userRepository.findByAccountNumber(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Validate max active auctions
        long activeCount = auctionRepository.countActiveAuctionsBySeller(sellerId);
        if (activeCount >= maxActiveAuctions) {
            throw new TooManyActiveAuctionsException(
                    "Cannot create more than " + maxActiveAuctions + " active auctions per player");
        }

        // Validate based on auction type
        if (request.getAuctionType() == AuctionType.BIN && request.getBinPrice() == null) {
            throw new IllegalArgumentException("BIN auctions require binPrice");
        }
        if (request.getAuctionType() == AuctionType.AUCTION && request.getStartingBid() == null) {
            throw new IllegalArgumentException("AUCTION type requires startingBid");
        }

        // Calculate end time from duration
        Instant startTime = Instant.now();
        Instant endTime = calculateEndTime(startTime, request.getDuration());

        // Create auction
        Auction auction = auctionMapper.toAuction(request, seller);
        auction.setStartTime(startTime);
        auction.setEndTime(endTime);
        auction.setStatus(AuctionStatus.ACTIVE);

        auction = auctionRepository.save(auction);
        return auctionMapper.toResponse(auction);
    }

    @Override
    public AuctionResponse getAuctionById(UUID id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found with ID: " + id));
        return auctionMapper.toResponse(auction);
    }

    @Override
    public Page<AuctionResponse> searchAuctions(String category, String auctionType, Long minPrice, Long maxPrice, String itemName, Pageable pageable) {
        AuctionType type = null;
        if (auctionType != null) {
            type = AuctionType.valueOf(auctionType.toUpperCase());
        }
        return auctionRepository.searchAuctions(category, type, minPrice, maxPrice, itemName, pageable)
                .map(auctionMapper::toResponse);
    }

    @Override
    public Page<AuctionResponse> getBrowseAuctions(Pageable pageable) {
        return auctionRepository.findActiveAuctionsSortByEndTime(pageable)
                .map(auctionMapper::toResponse);
    }

    @Override
    public BidResponse placeBid(UUID auctionId, UUID bidderId, Long amount) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        // Validate auction state
        if (!auction.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw new AuctionExpiredException("Auction is not active");
        }

        if (auction.getEndTime().isBefore(Instant.now())) {
            throw new AuctionExpiredException("Auction has ended");
        }

        // Validate bidder
        User bidder = userRepository.findByAccountNumber(bidderId)
                .orElseThrow(() -> new RuntimeException("Bidder not found"));

        if (auction.getSeller().getId().equals(bidderId)) {
            throw new CannotBidOnOwnAuctionException("Cannot bid on your own auction");
        }

        // Validate bid amount
        long minimumBid = auction.getCurrentBid() + minimumBidRaise;
        if (amount < minimumBid) {
            throw new BidTooLowException(auction.getCurrentBid(), minimumBid);
        }

        // Create REFUND claim for previous highest bidder if exists
        if (auction.getHighestBidderId() != null && !auction.getHighestBidderId().equals(bidderId)) {
            User previousBidder = userRepository.findByAccountNumber(auction.getHighestBidderId())
                    .orElse(null);
            if (previousBidder != null) {
                AuctionClaim refundClaim = AuctionClaim.builder()
                        .user(previousBidder)
                        .auction(auction)
                        .claimType(AuctionClaimType.REFUND)
                        .claimed(false)
                        .build();
                claimRepository.save(refundClaim);
            }
        }

        // Place bid
        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(bidder)
                .amount(amount)
                .build();
        bid = bidRepository.save(bid);

        // Update auction
        auction.setCurrentBid(amount);
        auction.setHighestBidderId(bidderId);
        auction.getBidderNotifications().add(bidderId);
        auctionRepository.save(auction);

        return bidMapper.toResponse(bid);
    }

    @Override
    public AuctionResponse buyInstant(UUID auctionId, UUID buyerId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (!auction.getAuctionType().equals(AuctionType.BIN)) {
            throw new IllegalArgumentException("Auction is not a BIN type");
        }

        if (auction.getStatus().equals(AuctionStatus.SOLD)) {
            throw new AuctionAlreadySoldException("Auction already sold");
        }

        User buyer = userRepository.findByAccountNumber(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (auction.getSeller().getId().equals(buyerId)) {
            throw new CannotBidOnOwnAuctionException("Cannot buy your own auction");
        }

        // Set status to SOLD
        auction.setStatus(AuctionStatus.SOLD);
        auction.setHighestBidderId(buyerId);
        auction.setClaimed(false);

        // Create claims
        AuctionClaim itemClaim = AuctionClaim.builder()
                .user(buyer)
                .auction(auction)
                .claimType(AuctionClaimType.ITEM)
                .claimed(false)
                .build();
        claimRepository.save(itemClaim);

        AuctionClaim coinsClaim = AuctionClaim.builder()
                .user(auction.getSeller())
                .auction(auction)
                .claimType(AuctionClaimType.COINS)
                .claimed(false)
                .build();
        claimRepository.save(coinsClaim);

        auction = auctionRepository.save(auction);
        return auctionMapper.toResponse(auction);
    }

    @Override
    public AuctionResponse cancelAuction(UUID auctionId, UUID sellerId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (!auction.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Only seller can cancel auction");
        }

        if (!auction.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw new RuntimeException("Can only cancel active auctions");
        }

        if (bidRepository.countBidsForAuction(auctionId) > 0) {
            throw new RuntimeException("Cannot cancel auction with existing bids");
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        // Create return item claim for seller
        AuctionClaim returnClaim = AuctionClaim.builder()
                .user(auction.getSeller())
                .auction(auction)
                .claimType(AuctionClaimType.ITEM)
                .claimed(false)
                .build();
        claimRepository.save(returnClaim);

        auction = auctionRepository.save(auction);
        return auctionMapper.toResponse(auction);
    }

    @Override
    public List<AuctionResponse> getSellerAuctions(UUID sellerId) {
        return auctionRepository.findActiveAuctionsBySeller(sellerId).stream()
                .map(auctionMapper::toResponse)
                .toList();
    }

    @Override
    public Page<AuctionClaimResponse> getUnclaimedItems(UUID userId, Pageable pageable) {
        return claimRepository.findUnclaimedByUser(userId, pageable)
                .map(claim -> {
                    Long coinAmount = null;
                    if (claim.getClaimType() == AuctionClaimType.COINS ||
                        claim.getClaimType() == AuctionClaimType.REFUND) {
                        // Calculate coin amount based on claim type
                        if (claim.getClaimType() == AuctionClaimType.COINS) {
                            coinAmount = claim.getAuction().getCurrentBid();
                        } else if (claim.getClaimType() == AuctionClaimType.REFUND) {
                            Optional<Bid> bidOpt = bidRepository.findLatestBidByBidderInAuction(
                                    userId, claim.getAuction().getId());
                            coinAmount = bidOpt.map(Bid::getAmount).orElse(0L);
                        }
                    }
                    return claimMapper.toResponse(claim, coinAmount);
                });
    }

    @Override
    public AuctionClaimResponse collectClaim(UUID claimId, UUID userId) {
        AuctionClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!claim.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cannot collect other user's claims");
        }

        claim.setClaimed(true);
        claim = claimRepository.save(claim);

        Long coinAmount = null;
        if (claim.getClaimType() == AuctionClaimType.COINS ||
            claim.getClaimType() == AuctionClaimType.REFUND) {
            if (claim.getClaimType() == AuctionClaimType.COINS) {
                coinAmount = claim.getAuction().getCurrentBid();
            } else if (claim.getClaimType() == AuctionClaimType.REFUND) {
                Optional<Bid> bidOpt = bidRepository.findLatestBidByBidderInAuction(
                        userId, claim.getAuction().getId());
                coinAmount = bidOpt.map(Bid::getAmount).orElse(0L);
            }
        }
        return claimMapper.toResponse(claim, coinAmount);
    }

    private Instant calculateEndTime(Instant startTime, String duration) {
        return switch (duration.toLowerCase()) {
            case "1h" -> startTime.plus(1, ChronoUnit.HOURS);
            case "6h" -> startTime.plus(6, ChronoUnit.HOURS);
            case "12h" -> startTime.plus(12, ChronoUnit.HOURS);
            case "24h" -> startTime.plus(24, ChronoUnit.HOURS);
            case "48h" -> startTime.plus(48, ChronoUnit.HOURS);
            default -> throw new IllegalArgumentException("Invalid duration: " + duration);
        };
    }
}
