package com.auction.app.domains.history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auction_history_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionHistoryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private UUID auctionId;

    @Enumerated(EnumType.STRING)
    private AuctionHistoryEventType eventType;

    private Long coinAmount;

    private Instant occurredAt;
}
