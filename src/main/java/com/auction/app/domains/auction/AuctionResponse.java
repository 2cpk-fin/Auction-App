package com.auction.app.domains.auction;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuctionResponse {
    private Long auctionId;
    private Long sellerId;
    private Long productId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}