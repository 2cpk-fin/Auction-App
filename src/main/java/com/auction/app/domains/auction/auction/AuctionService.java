package com.auction.app.domains.auction.auction;

import com.auction.app.domains.auction.auctionClaim.AuctionClaimResponse;
import com.auction.app.domains.bid.BidResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AuctionService {

    // User create auction here
    AuctionResponse createAuction(AuctionRequest request, UUID sellerId);

    AuctionResponse getAuctionById(UUID id);

    Page<AuctionResponse> searchAuctions(String category, String auctionType, Long minPrice, Long maxPrice, String itemName, Pageable pageable);

    Page<AuctionResponse> getBrowseAuctions(Pageable pageable);

    BidResponse placeBid(UUID auctionId, UUID bidderId, Long amount);

    AuctionResponse buyInstant(UUID auctionId, UUID buyerId);

    AuctionResponse cancelAuction(UUID auctionId, UUID sellerId);

    List<AuctionResponse> getSellerAuctions(UUID sellerId);

    Page<AuctionClaimResponse> getUnclaimedItems(UUID userId, Pageable pageable);

    AuctionClaimResponse collectClaim(UUID claimId, UUID userId);
}
