package com.auction.app.domains.bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.timestamp DESC")
    List<Bid> findByAuctionId(@Param("auctionId") UUID auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC LIMIT 1")
    Optional<Bid> findHighestBidForAuction(@Param("auctionId") UUID auctionId);

    @Query("SELECT b FROM Bid b WHERE b.bidder.id = :bidderId AND b.auction.id = :auctionId ORDER BY b.timestamp DESC LIMIT 1")
    Optional<Bid> findLatestBidByBidderInAuction(@Param("bidderId") UUID bidderId, @Param("auctionId") UUID auctionId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auction.id = :auctionId")
    long countBidsForAuction(@Param("auctionId") UUID auctionId);
}
