package com.auction.app.domains.auction.exceptions;

public class LockAcquisitionFailedException extends RuntimeException {
    public LockAcquisitionFailedException(String message) {
        super(message);
    }
}
