package com.auction.app.domains.bid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;

    public BidResponse getBidById(UUID bidId) {
        return bidRepository.findById(bidId)
                .map(new BidMapper()::toResponse)
                .orElseThrow(() -> new RuntimeException("Bid not found"));
    }
}
