package com.auction.app.domains.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductResponse> getProductsByUserId(Long userId) {
        List<Product> productList = productRepository.findByUserId(userId);

        List<ProductResponse> responseList = new ArrayList<>();

        for (Product product : productList) {
            ProductResponse response = productMapper.toProductResponse(product);
            responseList.add(response);
        }

        return responseList;
    }

    public ProductResponse addProduct(ProductRequest productRequest, Long userId) {
        // Create new product
        Product newProduct = productMapper.toProduct(productRequest, userId);
        // Save to DB
        productRepository.save(newProduct);
        // Return the response
        return productMapper.toProductResponse(newProduct);
    }

    public ProductResponse updateProduct(ProductRequest productRequest, Long userId, Long productId) {
        Product updatedProduct = productMapper.toProduct(productRequest, userId);
        updatedProduct.setProductId(productId);
        productRepository.save(updatedProduct);
        return productMapper.toProductResponse(updatedProduct);
    }

    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!Objects.equals(product.getOwner().getUserId(), userId)) {
            throw new RuntimeException("You are not the owner");
        }

        productRepository.deleteById(productId);
    }
}
