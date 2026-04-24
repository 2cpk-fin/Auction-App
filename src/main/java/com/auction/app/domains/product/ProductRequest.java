package com.auction.app.domains.product;

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
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 250, message = "Product name must not exceed 250 characters")
    private String productName;

    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Bid increment is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Bid increment must be greater than 0")
    private BigDecimal bidIncrement;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "At least one tag ID must be provided")
    @Size(min = 1, message = "Product must have at least one tag")
    private List<Long> tagIds;
}