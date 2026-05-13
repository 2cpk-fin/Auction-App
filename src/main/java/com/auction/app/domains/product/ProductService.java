package com.auction.app.domains.product;

import com.auction.app.domains.user.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductService {

    /**
     * Search within a specific user's storage using filters.
     */
    List<ProductResponse> searchStorage(String email, String keyword, Category category, Set<Tag> tags);

    /**
     * Adds a new product to the user's storage.
     */
    ProductResponse addProduct(User user, ProductRequest productRequest);

    /**
     * Updates an existing product in storage if the user owns it.
     */
    ProductResponse updateProduct(String email, ProductRequest productRequest, UUID productId);

    /**
     * Deletes a product from storage if the user owns it.
     */
    void deleteProduct(String email, UUID productId);
}