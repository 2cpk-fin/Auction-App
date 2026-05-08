package com.auction.app.domains.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionPlayerStatsRepository extends JpaRepository<AuctionPlayerStats, UUID> {

    Optional<AuctionPlayerStats> findByUserId(UUID userId);
}
