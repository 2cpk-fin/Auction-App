package com.auction.app.domains.auction.exceptions;

public class TooManyActiveAuctionsException extends RuntimeException {
    public TooManyActiveAuctionsException(String message) {
        super(message);
    }
}
