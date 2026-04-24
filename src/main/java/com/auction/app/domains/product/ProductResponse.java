package com.auction.app.domains.product;

import com.auction.app.domains.tag.TagResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal bidIncrement;
    private Integer quantity;
    private List<TagResponse> tags;
}