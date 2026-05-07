package com.auction.app.domains.auction;

import com.auction.app.domains.auction.auctionItem.AuctionItem;
import com.auction.app.domains.auction.auctionItem.AuctionItemMapper;
import com.auction.app.domains.product.Product;
import com.auction.app.domains.product.ProductRepository;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuctionMapper {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuctionItemMapper auctionItemMapper;

    public Auction toAuction(AuctionRequest request, User seller) {
        if (request == null) {
            return null;
        }

        Auction auction = new Auction();
        auction.setSeller(seller);
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStatus(AuctionStatus.UPCOMING);

        auction.setBidList(new ArrayList<>());
        List<AuctionItem> auctionItems = new ArrayList<>();

        if (request.getAuctionItems() != null) {
            for (var itemRequest : request.getAuctionItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemRequest.getProductId()));

                if (product.getQuantity() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Not enough products in stock for ID: " + product.getProductId());
                }

                AuctionItem item = auctionItemMapper.toAuctionItem(itemRequest, product);

                item.setAuction(auction);
                auctionItems.add(item);
            }
        }

        auction.setAuctionItems(auctionItems);

        return auction;
    }

    public AuctionResponse toResponse(Auction auction) {
        if (auction == null) {
            return null;
        }

        return AuctionResponse.builder()
                .auctionId(auction.getAuctionId())
                .seller(auction.getSeller() != null ? userMapper.userToResponse(auction.getSeller()) : null)
                .auctionItems(auction.getAuctionItems() != null ?
                        auction.getAuctionItems().stream()
                        .map(auctionItemMapper::toResponse)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
    }
}