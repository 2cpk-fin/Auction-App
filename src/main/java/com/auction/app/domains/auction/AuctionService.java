package com.auction.app.domains.auction;

import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private UserRepository userRepository;

    AuctionResponse createAuction(AuctionRequest auctionRequest, String email){
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Auction auction = auctionMapper.toAuction(auctionRequest, seller);
        auctionRepository.save(auction);
        return auctionMapper.toResponse(auction);
    }
}
