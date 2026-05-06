package com.auction.app.domains.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequest {
    private Long bidId;
    private Long sellerId;
    private Long bidderId;
    private BigDecimal amount;
    private TransactionType type;
}
