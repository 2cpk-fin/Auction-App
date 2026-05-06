package com.auction.app.domains.bid;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BidRequest {
    private Long auctionId;
    private Long bidderId;
    private Long productId;
    private BigDecimal bidPrice;
}
