package com.auction.app.domains.bid;

import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidResponse toResponse(Bid bid) {
        if (bid == null) {
            return null;
        }

        return BidResponse.builder()
                .id(bid.getId())
                .auctionId(bid.getAuction() != null ? bid.getAuction().getId() : null)
                .bidderId(bid.getBidder() != null ? bid.getBidder().getId() : null)
                .amount(bid.getAmount())
                .timestamp(bid.getTimestamp())
                .build();
    }
}
