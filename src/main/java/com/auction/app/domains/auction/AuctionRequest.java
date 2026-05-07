package com.auction.app.domains.auction;

import com.auction.app.domains.auction.auctionItem.AuctionItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuctionRequest {

    @NotBlank(message = "Auction title is required")
    private String title;

    private String description;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time cannot be in the past")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotEmpty(message = "You must include at least one item in the auction")
    @Valid
    private List<AuctionItemRequest> auctionItems;
}