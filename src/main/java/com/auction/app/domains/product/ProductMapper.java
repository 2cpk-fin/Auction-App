package com.auction.app.domains.product;

import com.auction.app.domains.user.User;
import org.springframework.stereotype.Component;
import java.util.HashSet;

@Component
public class ProductMapper {

    public Product toProduct(User owner, ProductRequest request) {
        return Product.builder()
                .productName(request.getProductName())
                .category(request.getCategory())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .owner(owner)
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .build();
    }

    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .tags(product.getTags())
                .build();
    }

    public void updateProductFromRequest(ProductRequest request, Product product) {
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        product.getTags().clear();
        if (request.getTags() != null) {
            product.getTags().addAll(request.getTags());
        }
    }
}