package com.auction.app.domains.auction;

import com.auction.app.domains.user.User;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    public Auction toAuction(AuctionRequest request, User seller) {
        if (request == null) {
            return null;
        }

        return Auction.builder()
                .seller(seller)
                .itemName(request.getItemName())
                .itemCategory(request.getItemCategory())
                .auctionType(request.getAuctionType())
                .startingBid(request.getStartingBid())
                .binPrice(request.getBinPrice())
                .currentBid(request.getStartingBid())
                .status(AuctionStatus.UPCOMING)
                .claimed(false)
                .build();
    }

    public AuctionResponse toResponse(Auction auction) {
        if (auction == null) {
            return null;
        }

        return AuctionResponse.builder()
                .id(auction.getId())
                .sellerId(auction.getSeller() != null ? auction.getSeller().getId() : null)
                .sellerName(auction.getSeller() != null ? auction.getSeller().getUsername() : null)
                .itemName(auction.getItemName())
                .itemCategory(auction.getItemCategory())
                .auctionType(auction.getAuctionType())
                .startingBid(auction.getStartingBid())
                .binPrice(auction.getBinPrice())
                .currentBid(auction.getCurrentBid())
                .highestBidderId(auction.getHighestBidderId())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .claimed(auction.getClaimed())
                .bidCount((long) auction.getBids().size())
                .build();
    }
}