package com.auction.app.domains.product;

import com.auction.app.domains.tag.Tag;
import com.auction.app.domains.tag.TagMapper;
import com.auction.app.domains.tag.TagRepository;
import com.auction.app.domains.tag.TagResponse;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserRepository userRepository;

    public Product toProduct(String email, ProductRequest productRequest) {
        Product product = new Product();

        product.setProductName(productRequest.getProductName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());

        // Set tags
        List<Tag> tags = new ArrayList<>();
        for (Long tagId : productRequest.getTagIds()) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found"));
            tags.add(tag);
        }
        product.setTags(tags);

        // Set user
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        product.setOwner(owner);

        return product;
    }

    public ProductResponse toProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setProductId(product.getProductId());
        productResponse.setProductName(product.getProductName());
        productResponse.setPrice(product.getPrice());
        productResponse.setQuantity(product.getQuantity());

        List<TagResponse> tagResponses = new ArrayList<>();
        for (Tag tag : product.getTags()) {
            tagResponses.add(tagMapper.toResponse(tag));
        }
        productResponse.setTags(tagResponses);

        return productResponse;
    }

    public void updateProductFromRequest(ProductRequest productRequest, Product product) {
        product.setProductName(productRequest.getProductName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());

        List<Tag> tags = new ArrayList<>();
        for (Long tagId : productRequest.getTagIds()) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found"));
            tags.add(tag);
        }
        product.setTags(tags);
    }
}
