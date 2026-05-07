package com.auction.app.domains.auction.exceptions;

public class BidTooLowException extends RuntimeException {
    private final Long currentBid;
    private final Long minimumRequired;

    public BidTooLowException(Long currentBid, Long minimumRequired) {
        super("Bid too low. Current bid: " + currentBid + ", Minimum required: " + minimumRequired);
        this.currentBid = currentBid;
        this.minimumRequired = minimumRequired;
    }

    public Long getCurrentBid() {
        return currentBid;
    }

    public Long getMinimumRequired() {
        return minimumRequired;
    }
}
