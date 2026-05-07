package com.auction.app.domains.auction.exceptions;

public class AuctionExpiredException extends RuntimeException {
    public AuctionExpiredException(String message) {
        super(message);
    }
}
