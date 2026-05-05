package com.auction.app.domains.auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionMapper auctionMapper;

    AuctionResponse createAuction(AuctionRequest auctionRequest){
        Auction auction = auctionMapper.toAuction(auctionRequest);
        auctionRepository.save(auction);
        return auctionMapper.toAuctionResponse(auction);
    }
}
