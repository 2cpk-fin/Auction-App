package com.auction.app.domains.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuctionHistoryRepository extends JpaRepository<AuctionHistoryEvent, UUID> {

    Page<AuctionHistoryEvent> findByUserIdOrderByOccurredAtDesc(UUID userId, Pageable pageable);
}
