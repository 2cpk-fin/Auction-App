package com.auction.app.domains.bid;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BidResponse {
    private Long bidId;
    private Long auctionId;
    private Long bidderId;
    private Long productId;
    private BigDecimal bidPrice;
    private LocalDateTime bidAt;
}