package com.auction.app.domains.bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("SELECT b FROM Bid b WHERE b.bidder.email = :email")
    List<Bid> findAllUserBids(@Param("email") String email);

    @Query("SELECT b FROM Bid b WHERE b.bidder.email = :email AND b.auction.auctionId = :auctionId")
    List<Bid> findAllUserBidsInOneAuction(@Param("auctionId") long auctionId, @Param("email") String email);


}
