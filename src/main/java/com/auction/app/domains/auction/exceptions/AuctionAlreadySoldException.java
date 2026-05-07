package com.auction.app.domains.auction.exceptions;

public class AuctionAlreadySoldException extends RuntimeException {
    public AuctionAlreadySoldException(String message) {
        super(message);
    }
}
