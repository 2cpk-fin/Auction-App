package com.auction.app.domains.auction.auction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionRequest {

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String itemCategory;

    @NotNull(message = "Auction type is required")
    private AuctionType auctionType;

    @NotNull(message = "Starting bid is required")
    @Positive(message = "Starting bid must be positive")
    private Long startingBid;

    private Long binPrice;

    @NotNull(message = "Duration is required")
    private String duration;
}