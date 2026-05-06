package com.auction.app.domains.auction;

import com.auction.app.domains.product.ProductRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionRequest {
    // Seller will send their id
    private Long sellerId;

    // And a list of products
    private List<UserProductRequest> productRequestList;

    // They can choose startTime and endTime
    // ( min(startTime) = now + 1 day )
    // ( max(endTime - startTime) = 1 day )
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

@Getter
class UserProductRequest {
    private Long productId;
    private Integer quantity;
}