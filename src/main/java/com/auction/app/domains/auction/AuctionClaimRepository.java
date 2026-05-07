package com.auction.app.domains.auction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuctionClaimRepository extends JpaRepository<AuctionClaim, UUID> {

    @Query("SELECT ac FROM AuctionClaim ac WHERE ac.user.id = :userId AND ac.claimed = false")
    Page<AuctionClaim> findUnclaimedByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT ac FROM AuctionClaim ac WHERE ac.user.id = :userId AND ac.claimed = false")
    List<AuctionClaim> findUnclaimedByUserList(@Param("userId") UUID userId);

    @Query("SELECT ac FROM AuctionClaim ac WHERE ac.auction.id = :auctionId")
    List<AuctionClaim> findByAuctionId(@Param("auctionId") UUID auctionId);
}
