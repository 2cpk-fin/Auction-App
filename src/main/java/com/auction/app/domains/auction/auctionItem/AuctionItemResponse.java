package com.auction.app.domains.auction.auctionItem;

import com.auction.app.domains.bid.BidderResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    private List<BidderResponse> topBids;
    private Integer totalBidsPlaced;
}
