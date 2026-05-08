package com.auction.app.domains.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionHistoryEventResponse {

    private UUID id;
    private UUID userId;
    private UUID auctionId;
    private AuctionHistoryEventType eventType;
    private Long coinAmount;
    private Instant occurredAt;
}
