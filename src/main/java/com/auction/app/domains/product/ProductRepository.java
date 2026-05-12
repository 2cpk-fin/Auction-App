package com.auction.app.domains.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT DISTINCT p.* FROM products p " +
            "JOIN users u ON u.user_id = p.owner_id " +
            "LEFT JOIN products_tags t ON p.product_id = t.product_id " +
            "WHERE u.email = :email " +
            "AND (:keyword IS NULL OR LOWER(CAST(p.product_name AS text)) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:tagIds IS NULL OR t.tag_id IN (:tagIds))", nativeQuery = true)
    List<Product> searchUserProducts(
            @Param("email") String email,
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds
    );

    @Query(value = "SELECT DISTINCT p.* FROM products p " +
            "JOIN users u ON u.user_id = p.owner_id " +
            "LEFT JOIN products_tags t ON p.product_id = t.product_id " +
            "WHERE u.user_id = :ownerId " +
            "AND (:keyword IS NULL OR LOWER(CAST(p.product_name AS text)) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:tagIds IS NULL OR t.tag_id IN (:tagIds))", nativeQuery = true)
    List<Product> searchUserProductsByOwnerId(
            @Param("ownerId") Long ownerId,
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds
    );

    @Query(value = "SELECT DISTINCT p.* FROM products p " +
            "JOIN users u ON u.user_id = p.owner_id " +
            "LEFT JOIN products_tags t ON p.product_id = t.product_id " +
            "WHERE u.user_id = :ownerId " +
            "AND (:keyword IS NULL OR LOWER(CAST(p.product_name AS text)) LIKE LOWER(CONCAT('%', :keyword, '%')))", nativeQuery = true)
    List<Product> searchUserProductsByOwnerIdNoTags(
            @Param("ownerId") Long ownerId,
            @Param("keyword") String keyword
    );

}
