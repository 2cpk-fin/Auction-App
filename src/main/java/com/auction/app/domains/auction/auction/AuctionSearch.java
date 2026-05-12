package com.auction.app.domains.auction.auction;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class AuctionSearch {

    @PersistenceContext
    private EntityManager em;

    /**
     * Tìm auctions theo username của seller (case-insensitive, partial match)
     */
    public List<Auction> findByUserName(String username) {
        String jpql = "SELECT a FROM Auction a WHERE LOWER(a.seller.username) LIKE LOWER(CONCAT('%', :username, '%'))";
        TypedQuery<Auction> q = em.createQuery(jpql, Auction.class);
        q.setParameter("username", username == null ? "" : username);
        return q.getResultList();
    }

    /**
     * Tìm auctions theo tên product (case-insensitive, partial match)
     */
    public List<Auction> findByProductName(String productName) {
        String jpql = "SELECT a FROM Auction a WHERE a.product IS NOT NULL AND LOWER(a.product.productName) LIKE LOWER(CONCAT('%', :productName, '%'))";
        TypedQuery<Auction> q = em.createQuery(jpql, Auction.class);
        q.setParameter("productName", productName == null ? "" : productName);
        return q.getResultList();
    }

    /**
     * Tìm auctions theo danh sách tag names. Trả về auctions có ít nhất một trong các tag được cung cấp.
     */
    public List<Auction> findByTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return List.of();
        String jpql = "SELECT DISTINCT a FROM Auction a JOIN a.auctionTags at JOIN at.tag t WHERE LOWER(t.tagName) IN :names";
        TypedQuery<Auction> q = em.createQuery(jpql, Auction.class);
        List<String> lowerNames = tagNames.stream().map(s -> s.toLowerCase()).toList();
        q.setParameter("names", lowerNames);
        return q.getResultList();
    }
}
