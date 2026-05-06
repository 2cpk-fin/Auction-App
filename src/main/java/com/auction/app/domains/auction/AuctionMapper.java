package com.auction.app.domains.auction;

import com.auction.app.domains.product.Product;
import com.auction.app.domains.product.ProductRepository;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserMapper;
import com.auction.app.domains.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuctionMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserMapper userMapper;

    Auction toAuction(AuctionRequest auctionRequest) {
        Auction auction = new Auction();

        User seller = userRepository.findById(auctionRequest.getSellerId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        List<Product> productList = new ArrayList<>();
        for (UserProductRequest request : auctionRequest.getProductRequestList()) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < request.getQuantity()) {
                throw new RuntimeException("Not enough products");
            }

            productList.add(product);
        }

        auction.setSeller(seller);
        auction.setProductList(productList);
        auction.setStartTime(auctionRequest.getStartTime());
        auction.setEndTime(auctionRequest.getEndTime());
        auction.setStatus(AuctionStatus.UPCOMING);

        return auction;
    }

    AuctionResponse toAuctionResponse(Auction auction) {
        AuctionResponse auctionResponse = new AuctionResponse();

        auctionResponse.setAuctionId(auction.getAuctionId());
        auctionResponse.setSeller(userMapper.userToResponse(auction.getSeller()));
        auctionResponse.setStartTime(auction.getStartTime());
        auctionResponse.setEndTime(auction.getEndTime());
        auctionResponse.setStatus(auction.getStatus());

        return auctionResponse;
    }
}
