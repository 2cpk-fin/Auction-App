package com.auction.app.domains.auction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT a FROM Auction a WHERE a.status = 'UPCOMING'")
    List<Auction> findAllUpcomingAuction();

    @Query("SElECT a From Auction a WHERE a.status = 'ACTIVE'")
    List<Auction> findAllActiveAuction();

    @Query(" SELECT a FROM Auction a WHERE a.status = 'COMPLETE'")
    List<Auction> findAllCompleteAuction();

    @Query("SELECT a From Auction a WHERE a.status = 'CANCELLED'")
    List<Auction> findAllCancelledAuction();
}
