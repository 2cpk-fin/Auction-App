package com.auction.app.domains.auction.auctionItem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AuctionItemResponse {

    private Long auctionItemId;

    private Long productId;
    private String productName;

    private Integer quantity;
    private BigDecimal startingPrice;
    private BigDecimal bidIncrement;

    private BigDecimal currentHighestBid;
    private Integer totalBidsPlaced;
}
