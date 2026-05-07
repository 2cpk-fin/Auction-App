package com.auction.app.domains.auction;

import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public AuctionResponse createAuction(AuctionRequest auctionRequest, String email) {

    }

    public AuctionResponse updateAuction(AuctionRequest auctionRequest, String email, long auctionId) {

    }

    public void deleteAuction(long auctionId) {

    }

    public List<AuctionResponse> showAllUserAuctions(String email) {

    }

    public Auction findAuctionById(long id) {
        return auctionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Auction with id: " + id + " not found"));
    }


    public List<AuctionResponse> getAllUpcomingAuction() {
        List<Auction> auctionList = auctionRepository.findAllUpcomingAuction();

        List<AuctionResponse> auctionResponses = new ArrayList<>();

        for (Auction auction : auctionList) {
            auctionResponses.add(auctionMapper.toResponse(auction));
        }

        return auctionResponses;
    }

    public List<AuctionResponse> getAllActiveAuction() {
        List<Auction> auctionList = auctionRepository.findAllActiveAuction();
        List<AuctionResponse> auctionResponses = new ArrayList<>();
        for (Auction auction : auctionList) {
            auctionResponses.add(auctionMapper.toResponse(auction));
        }
        return auctionResponses;
    }
    public List<AuctionResponse> getAllCompletedAuction() {
        List<Auction> autionList =auctionRepository.findAllCompleteAuction();
        List<AuctionResponse> auctionResponses = new ArrayList<>();
        for (Auction auction : autionList) {
            auctionResponses.add(auctionMapper.toResponse(auction));

        }
        return auctionResponses;

    }
    public List<AuctionResponse> getAllCanceledAuction() {
        List<Auction> autionList = auctionRepository.findAllCancelledAuction();
        List<AuctionResponse> auctionResponses = new ArrayList<>();
        for (Auction auction : autionList) {
            auctionResponses.add(auctionMapper.toResponse(auction));

        }
        return auctionResponses;


    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
    }
}
