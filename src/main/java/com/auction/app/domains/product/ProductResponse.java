package com.auction.app.domains.product;

import lombok.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String productName;
    private Category category;
    private BigDecimal price;
    private Integer quantity;
    private Set<Tag> tags;
}