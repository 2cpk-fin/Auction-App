package com.auction.app.domains.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
    private Long txId;
    private Long bidId;
    private Long sellerId;
    private Long bidderId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}