package com.auction.app.domains.auction.auctionClaim;

import org.springframework.stereotype.Component;

@Component
public class AuctionClaimMapper {

    public AuctionClaimResponse toResponse(AuctionClaim claim, Long coinAmount) {
        if (claim == null) {
            return null;
        }

        return AuctionClaimResponse.builder()
                .id(claim.getId())
                .userId(claim.getUser() != null ? claim.getUser().getId() : null)
                .auctionId(claim.getAuction() != null ? claim.getAuction().getId() : null)
                .itemName(claim.getAuction() != null ? claim.getAuction().getItemName() : null)
                .coinAmount(coinAmount)
                .claimType(claim.getClaimType())
                .claimed(claim.getClaimed())
                .createdAt(claim.getCreatedAt())
                .build();
    }
}
