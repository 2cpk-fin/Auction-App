package com.auction.app.domains.auction.auctionItem;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auction_items")
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    // Links to the physical Product in storage
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "starting_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal startingPrice;

    @Column(name = "highest_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal highestPrice;

    @Column(name = "bid_increment", precision = 15, scale = 2, nullable = false)
    private BigDecimal bidIncrement;

}