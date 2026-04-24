package com.auction.app.domains.product;

import com.auction.app.domains.auction.Auction;
import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.tag.Tag;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 250, message = "Product name must not exceed 250 characters")
    @Column(name = "product_name", length = 250, nullable = false)
    private String productName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be strictly greater than 0")
    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Bid increment must be strictly greater than 0")
    @Column(name = "bid_increment")
    private BigDecimal bidIncrement;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // (Product - Tag)
    @ManyToMany
    @JoinTable(
            name = "products_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    // Each product has an owner (User - Product)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Many products can be sold in one auction session (Auction - Product)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    // Each bid can bid only one product only (Product - Bid)
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bid bid;
}