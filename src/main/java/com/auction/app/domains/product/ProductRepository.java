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

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.owner u " +
            "LEFT JOIN p.tags t " +
            "WHERE u.email = :email " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    List<Product> searchUserProducts(
            @Param("email") String email,
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds
    );

}
