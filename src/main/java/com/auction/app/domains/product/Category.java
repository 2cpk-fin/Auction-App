package com.auction.app.domains.product;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS("Electronics"),
    FASHION("Fashion & Accessories"),
    HOME_GARDEN("Home & Garden"),
    COLLECTIBLES("Collectibles & Art"),
    MOTORS("Vehicles & Parts"),
    SPORTS("Sporting Goods"),
    TOYS("Toys & Hobbies"),
    BUSINESS_INDUSTRIAL("Business & Industrial"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}