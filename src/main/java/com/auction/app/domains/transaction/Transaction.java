package com.auction.app.domains.transaction;

import com.auction.app.domains.bid.Bid;
import com.auction.app.domains.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txId;

    // Each transaction store one bid only (Transaction - Bid)
    @NotNull(message = "Winning bid reference is required")
    @OneToOne
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    // Seller can have many transactions due to they can host many auction sessions (Transaction - User)
    @NotNull(message = "Seller reference is required")
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Bidder can have many transactions in one auction sessions (Transaction - User)
    @NotNull(message = "Bidder reference is required")
    @ManyToOne
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    // Store the transferred amount
    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    // The transaction type
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant createdAt;
}