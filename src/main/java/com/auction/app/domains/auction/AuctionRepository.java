package com.auction.app.domains.auction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId AND a.status IN ('ACTIVE', 'UPCOMING')")
    List<Auction> findActiveAuctionsBySeller(@Param("sellerId") UUID sellerId);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.seller.id = :sellerId AND a.status IN ('ACTIVE', 'UPCOMING')")
    long countActiveAuctionsBySeller(@Param("sellerId") UUID sellerId);

    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND a.endTime <= :now")
    List<Auction> findExpiredAuctions(@Param("now") Instant now);

    @Query("SELECT a FROM Auction a WHERE a.status IN ('ACTIVE', 'UPCOMING') ORDER BY a.endTime ASC")
    Page<Auction> findActiveAuctionsSortByEndTime(Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE " +
           "a.status IN ('ACTIVE', 'UPCOMING') AND " +
           "(:category IS NULL OR a.itemCategory = :category) AND " +
           "(:auctionType IS NULL OR a.auctionType = :auctionType) AND " +
           "(:minPrice IS NULL OR a.currentBid >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.currentBid <= :maxPrice) AND " +
           "(:itemName IS NULL OR LOWER(a.itemName) LIKE LOWER(CONCAT('%', :itemName, '%')))")
    Page<Auction> searchAuctions(@Param("category") String category,
                                 @Param("auctionType") AuctionType auctionType,
                                 @Param("minPrice") Long minPrice,
                                 @Param("maxPrice") Long maxPrice,
                                 @Param("itemName") String itemName,
                                 Pageable pageable);

    Optional<Auction> findByIdAndStatus(UUID id, AuctionStatus status);
}
