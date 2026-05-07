package com.auction.app.domains.auction;

import com.auction.app.domains.auction.auctionItem.AuctionItemResponse;
import com.auction.app.domains.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AuctionResponse {
    private Long auctionId;

    private UserResponse seller;

    private List<AuctionItemResponse> auctionItems;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}