package com.auction.app.domains.auction;

import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public String registerAuction(long auctionId, String email) {
        User user = findByEmail(email);
        Auction auction = findAuctionById(auctionId);

        boolean isRegistered = auction.getRegisteredUsers().get(user.getUserId());
        if (isRegistered) {
            return "User is already registered for this auction";
        }

        auction.getRegisteredUsers().put(user.getUserId(), true);
        return "Successfully registered auction";
    }

    public Auction findAuctionById(long id) {
        return auctionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Auction with id: " + id + " not found"));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
    }
}
