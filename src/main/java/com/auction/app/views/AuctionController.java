package com.auction.app.views;

import com.auction.app.domains.auction.auction.AuctionRequest;
import com.auction.app.domains.auction.auction.AuctionResponse;
import com.auction.app.domains.auction.auction.AuctionService;
import com.auction.app.domains.auction.auctionClaim.AuctionClaimResponse;
import com.auction.app.domains.bid.BidRequest;
import com.auction.app.domains.bid.BidResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Auction management endpoints")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @Operation(summary = "Create a new auction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Auction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid auction request"),
            @ApiResponse(responseCode = "429", description = "Too many active auctions")
    })
    public ResponseEntity<AuctionResponse> createAuction(
            @RequestBody AuctionRequest request,
            Authentication authentication) {
        UUID sellerId = (UUID) authentication.getPrincipal();
        AuctionResponse response = auctionService.createAuction(request, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get auction by ID")
    @ApiResponse(responseCode = "200", description = "Auction found")
    @ApiResponse(responseCode = "404", description = "Auction not found")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @GetMapping
    @Operation(summary = "Browse and search auctions")
    @ApiResponse(responseCode = "200", description = "Auctions retrieved")
    public ResponseEntity<Page<AuctionResponse>> searchAuctions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String auctionType,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String itemName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (category != null || auctionType != null || minPrice != null || maxPrice != null || itemName != null) {
            return ResponseEntity
                    .ok(auctionService.searchAuctions(category, auctionType, minPrice, maxPrice, itemName, pageable));
        } else {
            return ResponseEntity.ok(auctionService.getBrowseAuctions(pageable));
        }
    }

    @PostMapping("/{id}/bid")
    @Operation(summary = "Place a bid on an auction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid placed successfully"),
            @ApiResponse(responseCode = "400", description = "Bid too low or auction expired"),
            @ApiResponse(responseCode = "403", description = "Cannot bid on own auction"),
            @ApiResponse(responseCode = "404", description = "Auction not found"),
            @ApiResponse(responseCode = "503", description = "Lock acquisition failed")
    })
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable UUID id,
            @RequestBody BidRequest request,
            Authentication authentication) {
        UUID bidderId = (UUID) authentication.getPrincipal();
        BidResponse response = auctionService.placeBid(id, bidderId, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/buy")
    @Operation(summary = "Buy an item instantly (BIN auctions only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item purchased successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid auction type"),
            @ApiResponse(responseCode = "403", description = "Cannot buy own auction"),
            @ApiResponse(responseCode = "404", description = "Auction not found"),
            @ApiResponse(responseCode = "409", description = "Auction already sold")
    })
    public ResponseEntity<AuctionResponse> buyInstant(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID buyerId = (UUID) authentication.getPrincipal();
        AuctionResponse response = auctionService.buyInstant(id, buyerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an auction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction cancelled"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel auction with bids"),
            @ApiResponse(responseCode = "403", description = "Only seller can cancel"),
            @ApiResponse(responseCode = "404", description = "Auction not found")
    })
    public ResponseEntity<AuctionResponse> cancelAuction(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID sellerId = (UUID) authentication.getPrincipal();
        AuctionResponse response = auctionService.cancelAuction(id, sellerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/my-auctions")
    @Operation(summary = "Get current user's active auctions")
    @ApiResponse(responseCode = "200", description = "Auctions retrieved")
    public ResponseEntity<java.util.List<AuctionResponse>> getMyAuctions(
            Authentication authentication) {
        UUID sellerId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(auctionService.getSellerAuctions(sellerId));
    }

    @GetMapping("/claims/pending")
    @Operation(summary = "Get unclaimed items and coins")
    @ApiResponse(responseCode = "200", description = "Claims retrieved")
    public ResponseEntity<Page<AuctionClaimResponse>> getUnclaimedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(auctionService.getUnclaimedItems(userId, pageable));
    }

    @PostMapping("/claims/{claimId}/collect")
    @Operation(summary = "Collect a claim (item or coins)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Claim collected"),
            @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<AuctionClaimResponse> collectClaim(
            @PathVariable UUID claimId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        AuctionClaimResponse response = auctionService.collectClaim(claimId, userId);
        return ResponseEntity.ok(response);
    }
}
