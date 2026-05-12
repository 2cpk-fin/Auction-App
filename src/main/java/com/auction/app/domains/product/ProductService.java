package com.auction.app.domains.product;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.auction.app.domains.user.UserRepository;
import com.auction.app.domains.user.User;

@Service
public class ProductService {
    /*
        My Storage View
        - Users can add products to their storage
        - Users can see what they have in the storage
        - They can search for product in their storage as well
        - They can also change the description of the product or delete it
    */


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> searchUserProducts(String email, String keyword, List<Long> tagIds) {
        // Resolve owner id from email, then query by owner id to ensure matching owner_id column
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
        Long ownerId = userOpt.get().getUserId();
        if (tagIds == null || tagIds.isEmpty()) {
        return productRepository.searchUserProductsByOwnerIdNoTags(ownerId, keyword)
            .stream()
            .map(productMapper::toProductResponse)
            .toList();
        }
        return productRepository.searchUserProductsByOwnerId(ownerId, keyword, tagIds)
            .stream()
            .map(productMapper::toProductResponse)
            .toList();
        }
        return List.of();
    }

    public ProductResponse addProduct(String email, ProductRequest productRequest) {
        Product newProduct = productMapper.toProduct(email, productRequest);
        productRepository.save(newProduct);
        return productMapper.toProductResponse(newProduct);
    }

    public ProductResponse updateProduct(String email, ProductRequest productRequest, Long productId) {
        Product product = findById(productId, email);
        productMapper.updateProductFromRequest(productRequest, product);
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(String email, Long productId) {
        findById(productId, email);
        productRepository.deleteById(productId);
    }

    private Product findById(Long productId, String email) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (!Objects.equals(product.getOwner().getEmail(), email)) throw new AccessDeniedException("You don't have permission to this product");
        return product;
    }
}
