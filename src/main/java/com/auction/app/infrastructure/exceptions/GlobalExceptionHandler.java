package com.auction.app.infrastructure.exceptions;

import com.auction.app.domains.auction.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAuctionNotFound(AuctionNotFoundException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "AUCTION_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BidTooLowException.class)
    public ResponseEntity<Map<String, Object>> handleBidTooLow(BidTooLowException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "BID_TOO_LOW"
        );
        response.put("currentBid", ex.getCurrentBid());
        response.put("minimumRequired", ex.getMinimumRequired());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuctionExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleAuctionExpired(AuctionExpiredException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "AUCTION_EXPIRED"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CannotBidOnOwnAuctionException.class)
    public ResponseEntity<Map<String, Object>> handleCannotBidOnOwnAuction(CannotBidOnOwnAuctionException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                "CANNOT_BID_ON_OWN_AUCTION"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AuctionAlreadySoldException.class)
    public ResponseEntity<Map<String, Object>> handleAuctionAlreadySold(AuctionAlreadySoldException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "AUCTION_ALREADY_SOLD"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(TooManyActiveAuctionsException.class)
    public ResponseEntity<Map<String, Object>> handleTooManyActiveAuctions(TooManyActiveAuctionsException ex) {
        Map<String, Object> response = buildErrorResponse(
                429,
                ex.getMessage(),
                "TOO_MANY_ACTIVE_AUCTIONS"
        );
        return ResponseEntity.status(429).body(response);
    }

    @ExceptionHandler(LockAcquisitionFailedException.class)
    public ResponseEntity<Map<String, Object>> handleLockAcquisitionFailed(LockAcquisitionFailedException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                ex.getMessage(),
                "LOCK_ACQUISITION_FAILED"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "INVALID_REQUEST"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                "INTERNAL_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private Map<String, Object> buildErrorResponse(int status, String message, String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("code", code);
        response.put("timestamp", Instant.now());
        return response;
    }
}
