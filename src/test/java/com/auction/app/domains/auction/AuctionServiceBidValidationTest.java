package com.auction.app.domains.auction;

import com.auction.app.domains.auction.auction.*;
import com.auction.app.domains.auction.auctionClaim.AuctionClaimRepository;
import com.auction.app.domains.auction.exceptions.BidTooLowException;
import com.auction.app.domains.auction.exceptions.CannotBidOnOwnAuctionException;
import com.auction.app.domains.auction.exceptions.AuctionExpiredException;
import com.auction.app.domains.bid.BidRepository;
import com.auction.app.domains.user.User;
import com.auction.app.domains.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auction Bid Validation Tests")
public class AuctionServiceBidValidationTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionClaimRepository auctionClaimRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private UUID auctionId;
    private UUID bidderId;
    private UUID sellerId;
    private Auction auction;
    private User bidder;
    private User seller;

    @BeforeEach
    void setUp() {
        auctionId = UUID.randomUUID();
        bidderId = UUID.randomUUID();
        sellerId = UUID.randomUUID();

        seller = User.builder()
                .accountNumber(sellerId)
                .username("seller")
                .build();

        bidder = User.builder()
                .accountNumber(bidderId)
                .username("bidder")
                .build();

        auction = Auction.builder()
                .id(auctionId)
                .seller(seller)
                .itemName("Test Item")
                .auctionType(AuctionType.AUCTION)
                .startingBid(100L)
                .currentBid(100L)
                .status(AuctionStatus.ACTIVE)
                .startTime(Instant.now().minusSeconds(3600))
                .endTime(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    @DisplayName("Should place bid successfully when bid exceeds current bid")
    void testPlaceBidSuccessfully() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(bidderId)).thenReturn(Optional.of(bidder));
        when(bidRepository.countBidsForAuction(auctionId)).thenReturn(0L);
        when(auctionRepository.save(any())).thenReturn(auction);

        var response = auctionService.placeBid(auctionId, bidderId, 150L);

        assertNotNull(response);
        assertEquals(150L, response.getAmount());
        verify(auctionRepository).save(any(Auction.class));
    }

    @Test
    @DisplayName("Should reject bid when bid is too low")
    void testPlaceBidTooLow() {
        auction.setCurrentBid(100L);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(bidderId)).thenReturn(Optional.of(bidder));

        BidTooLowException exception = assertThrows(BidTooLowException.class, () -> {
            auctionService.placeBid(auctionId, bidderId, 100L);
        });

        assertEquals(100L, exception.getCurrentBid());
        assertEquals(101L, exception.getMinimumRequired());
    }

    @Test
    @DisplayName("Should reject bid when seller bids on own auction")
    void testCannotBidOnOwnAuction() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(sellerId)).thenReturn(Optional.of(seller));

        CannotBidOnOwnAuctionException exception = assertThrows(CannotBidOnOwnAuctionException.class, () -> {
            auctionService.placeBid(auctionId, sellerId, 150L);
        });

        assertEquals("Cannot bid on your own auction", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject bid when auction has expired")
    void testBidOnExpiredAuction() {
        auction.setEndTime(Instant.now().minusSeconds(10));
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(bidderId)).thenReturn(Optional.of(bidder));

        AuctionExpiredException exception = assertThrows(AuctionExpiredException.class, () -> {
            auctionService.placeBid(auctionId, bidderId, 150L);
        });

        assertEquals("Auction has ended", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject bid when auction is not active")
    void testBidOnInactiveAuction() {
        auction.setStatus(AuctionStatus.SOLD);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(bidderId)).thenReturn(Optional.of(bidder));

        AuctionExpiredException exception = assertThrows(AuctionExpiredException.class, () -> {
            auctionService.placeBid(auctionId, bidderId, 150L);
        });

        assertEquals("Auction is not active", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept bid equal to minimum required bid")
    void testPlaceBidEqualToMinimum() {
        auction.setCurrentBid(100L);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(userRepository.findByAccountNumber(bidderId)).thenReturn(Optional.of(bidder));
        when(bidRepository.countBidsForAuction(auctionId)).thenReturn(0L);
        when(auctionRepository.save(any())).thenReturn(auction);

        var response = auctionService.placeBid(auctionId, bidderId, 101L);

        assertNotNull(response);
        assertEquals(101L, response.getAmount());
    }
}
