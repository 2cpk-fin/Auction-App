package com.auction.app.domains.bid;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class BidderResponse {
    private String username;
    private String profileImage;
    private BigDecimal bidPrice;
    private Instant bidAt;
}
