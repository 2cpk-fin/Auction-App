package com.auction.app.domains.bid;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.auction.AuctionRepository;
import com.auction.app.domains.auction.auctionItem.AuctionItem;
import com.auction.app.domains.auction.auctionItem.AuctionItemRepository;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionItemRepository auctionItemRepository;

    private final BidMapper bidMapper;

    public List<BidResponse> findAllUserBids(String email) {
        return bidRepository.findAllUserBids(email)
                .stream()
                .map(bidMapper::toResponse)
                .toList();
    }

    public List<BidResponse> findAllUserBidsInOneAuction(long auctionId, String email) {
        return bidRepository.findAllUserBidsInOneAuction(auctionId, email)
                .stream()
                .map(bidMapper::toResponse)
                .toList();
    }

    @Transactional
    public BidResponse createBid(BidRequest bidRequest, String email) {
        // Check for the authentication and user first
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));

        // Next, we check if they have already registered or not
        long auctionId = bidRequest.getAuctionId();
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new EntityNotFoundException("Auction with id: " + auctionId + " not found"));
        if (!isRegistered(user.getUserId(), auction)) {
            throw new RuntimeException("User is not registered in this auction");
        }

        // Then, we have to check if the bid price is higher than the current highest price or not
        AuctionItem auctionItem = auctionItemRepository.findByIdWithPessimisticLock(bidRequest.getAuctionItemId())
                .orElseThrow(() -> new RuntimeException("Auction Item not found"));
        if (bidRequest.getBidPrice().compareTo(auctionItem.getHighestPrice()) <= 0) {
            throw new RuntimeException("Someone placed a higher bid while you were typing!");
        }

        // If nothing wrong then create new bid
        Bid newBid = Bid.builder()
                .bidder(user)
                .auction(auction)
                .auctionItem(auctionItem)
                .bidPrice(bidRequest.getBidPrice())
                .bidAt(Instant.now())
                .build();
        bidRepository.save(newBid);

        return BidResponse.builder()
                .bidId(newBid.getBidId())
                .itemName(newBid.getAuctionItem().getProduct().getProductName())
                .bidPrice(newBid.getBidPrice())
                .bidAt(newBid.getBidAt())
                .build();
    }

    private boolean isRegistered(long userId, Auction auction) {
        return auction.getRegisteredUsers().get(userId);
    }
}
