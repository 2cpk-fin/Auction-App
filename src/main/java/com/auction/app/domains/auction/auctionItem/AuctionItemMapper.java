package com.auction.app.domains.auction.auctionItem;

import com.auction.app.domains.product.Product;
import com.auction.app.domains.bid.Bid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AuctionItemMapper {

    public AuctionItem toAuctionItem(AuctionItemRequest request, Product product) {
        if (request == null) {
            return null;
        }

        return AuctionItem.builder()
                .product(product)
                .quantity(request.getQuantity())
                .startingPrice(request.getStartingPrice())
                .bidIncrement(request.getBidIncrement())
                .build();
    }

    public AuctionItemResponse toResponse(AuctionItem entity) {
        if (entity == null) {
            return null;
        }

        List<Bid> bids = entity.getBids();
        int totalBidsPlaced = (bids != null) ? bids.size() : 0;

        // Default to the starting price if there are no bids yet
        BigDecimal currentHighestBid = entity.getStartingPrice();

        if (bids != null && !bids.isEmpty()) {
            currentHighestBid = bids.stream()
                    .map(Bid::getBidPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(entity.getStartingPrice());
        }

        return AuctionItemResponse.builder()
                .auctionItemId(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getProductName() : null)
                .quantity(entity.getQuantity())
                .startingPrice(entity.getStartingPrice())
                .bidIncrement(entity.getBidIncrement())
                .totalBidsPlaced(totalBidsPlaced)
                .build();
    }

    public AuctionItemResponse updateItemAndReturnResponse(
            AuctionItem existingEntity,
            AuctionItemRequest request,
            Product newProduct) {

        if (existingEntity == null || request == null) {
            return null;
        }

        if (newProduct != null) {
            existingEntity.setProduct(newProduct);
        }
        existingEntity.setQuantity(request.getQuantity());
        existingEntity.setStartingPrice(request.getStartingPrice());
        existingEntity.setBidIncrement(request.getBidIncrement());

        return toResponse(existingEntity);
    }
}