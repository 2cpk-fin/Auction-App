package com.auction.app.domains.bid;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidRequest {
    @NotNull(message = "Auction ID is required")
    private UUID auctionId;

    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private Long amount;
}
