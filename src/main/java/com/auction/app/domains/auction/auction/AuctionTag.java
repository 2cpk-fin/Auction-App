package com.auction.app.domains.auction.auction;

import com.auction.app.domains.auction.auction.Tag;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auction_tags")
public class AuctionTag {

    @EmbeddedId
    private AuctionTagKey id;

    @ManyToOne
    @MapsId("auctionId")
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // Thêm các trường phụ nếu cần
    private java.time.Instant createdAt = java.time.Instant.now();
}