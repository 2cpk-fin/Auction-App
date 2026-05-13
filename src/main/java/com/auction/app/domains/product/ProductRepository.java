package com.auction.app.domains.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.tags t " +
            "WHERE p.owner.email = :email " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:tags IS NULL OR t IN :tags)")
    List<Product> searchingStorage(
            @Param("email") String email,
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("tags") Set<Tag> tags
    );
}