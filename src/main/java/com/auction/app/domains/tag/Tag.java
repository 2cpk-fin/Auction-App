package com.auction.app.domains.tag;

import com.auction.app.domains.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @NotBlank(message = "Tag name cannot be blank")
    @Size(max = 50, message = "Tag name must not exceed 50 characters")
    @Column(name = "tag_name", length = 50, nullable = false)
    private String tagName;

    // (Product - Tag)
    @ManyToMany(mappedBy = "tags")
    private List<Product> products;
}