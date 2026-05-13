package com.auction.app.domains.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;

    private Set<Tag> tags;
}