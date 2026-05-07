package com.auction.app.domains.auction.exceptions;

public class CannotBidOnOwnAuctionException extends RuntimeException {
    public CannotBidOnOwnAuctionException(String message) {
        super(message);
    }
}
