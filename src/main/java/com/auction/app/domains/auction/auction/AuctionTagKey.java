package com.auction.app.domains.auction.auction;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class AuctionTagKey implements Serializable {
    private UUID auctionId;
    private Long tagId;
}