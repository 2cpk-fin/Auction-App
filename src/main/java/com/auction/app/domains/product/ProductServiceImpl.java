package com.auction.app.domains.product;

import com.auction.app.domains.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchStorage(String email, String keyword, Category category, Set<Tag> tags) {
        return productRepository.searchingStorage(email, keyword, category, tags)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse addProduct(User user, ProductRequest productRequest) {
        Product newProduct = productMapper.toProduct(user, productRequest);
        productRepository.save(newProduct);
        return productMapper.toProductResponse(newProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String email, ProductRequest productRequest, UUID productId) {
        Product product = findAndVerifyOwner(productId, email);
        productMapper.updateProductFromRequest(productRequest, product);
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String email, UUID productId) {
        Product product = findAndVerifyOwner(productId, email);
        productRepository.delete(product);
    }

    private Product findAndVerifyOwner(UUID productId, String email) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("You do not have permission to access this product");
        }
        return product;
    }
}