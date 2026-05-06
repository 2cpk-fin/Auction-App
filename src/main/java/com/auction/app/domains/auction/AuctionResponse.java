package com.auction.app.domains.auction;

import com.auction.app.domains.product.ProductResponse;
import com.auction.app.domains.user.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionResponse {
    private Long auctionId;
    private UserResponse seller;
    private List<ProductResponse> productList;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
}