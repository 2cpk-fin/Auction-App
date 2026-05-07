package com.auction.app.domains.bid;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.auction.AuctionRepository;
import com.auction.app.domains.auction.auctionItem.AuctionItem;
import com.auction.app.domains.auction.auctionItem.AuctionItemRepository;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserMapper;
import com.auction.app.domains.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BidMapper {

    private final AuctionRepository auctionRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public Bid toBid(BidRequest bidRequest, String email) {
        if (bidRequest == null) {
            return null;
        }

        return Bid.builder()
                .bidder(findUserByEmail(email))
                .auction(findAuctionById(bidRequest.getAuctionId()))
                .auctionItem(findAuctionItemById(bidRequest.getAuctionItemId()))
                .bidPrice(bidRequest.getBidPrice())
                .bidAt(Instant.now())
                .build();
    }

    public BidResponse toResponse(Bid bid) {
        if (bid == null) {
            return null;
        }

        return BidResponse.builder()
                .bidId(bid.getBidId())
                .itemName(bid.getAuctionItem().getProduct().getProductName())
                .bidPrice(bid.getBidPrice())
                .bidAt(bid.getBidAt())
                .build();
    }

    private Auction findAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId).orElseThrow(() -> new EntityNotFoundException("Auction with id: " + auctionId + " not found"));
    }

    private AuctionItem findAuctionItemById(Long auctionItemId) {
        return auctionItemRepository.findById(auctionItemId).orElseThrow(() -> new EntityNotFoundException("Item with id: " + auctionItemId + " not found"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }
}
