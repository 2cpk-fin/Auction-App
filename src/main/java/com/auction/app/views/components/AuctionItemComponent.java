package com.auction.app.views.components;

import com.auction.app.domains.auction.auction.AuctionResponse;
import com.auction.app.domains.auction.auction.AuctionType;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AuctionItemComponent extends HBox {

    private final ApplicationContext springContext;

    @FXML
    private Label itemNameLabel;
    @FXML
    private Label auctionTypeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label timeRemainingLabel;

    private AuctionResponse auction;
    private Consumer<AuctionResponse> onAuctionSelected;
    private Timeline updateTimeline;

    public AuctionItemComponent() {
        this(null);
    }

    public void setAuction(AuctionResponse auction) {
        this.auction = auction;
        updateUI();
        startTimeUpdater();
    }

    public void setOnAuctionSelected(Consumer<AuctionResponse> handler) {
        this.onAuctionSelected = handler;
    }

    private void updateUI() {
        if (auction == null)
            return;

        if (itemNameLabel != null) {
            itemNameLabel.setText(auction.getItemName());
        }

        if (auctionTypeLabel != null) {
            String typeText = auction.getAuctionType() == AuctionType.BIN ? "BIN" : "AUCTION";
            auctionTypeLabel.setText(typeText);
            String color = auction.getAuctionType() == AuctionType.BIN ? "#10B981" : "#4F46E5";
            auctionTypeLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        }

        if (priceLabel != null) {
            String priceText = auction.getAuctionType() == AuctionType.BIN
                    ? "$" + auction.getBinPrice()
                    : "$" + auction.getCurrentBid();
            priceLabel.setText(priceText);
        }

        updateTimeRemaining();
    }

    private void updateTimeRemaining() {
        if (auction == null || timeRemainingLabel == null)
            return;

        Instant now = Instant.now();
        Instant endTime = auction.getEndTime();

        if (endTime.isBefore(now)) {
            timeRemainingLabel.setText("Ended");
            timeRemainingLabel.setStyle("-fx-text-fill: #EF4444;");
        } else {
            long secondsRemaining = ChronoUnit.SECONDS.between(now, endTime);
            long hours = secondsRemaining / 3600;
            long minutes = (secondsRemaining % 3600) / 60;
            long seconds = secondsRemaining % 60;

            String timeText = String.format("%dh %dm %ds", hours, minutes, seconds);
            timeRemainingLabel.setText(timeText);
            timeRemainingLabel.setStyle("-fx-text-fill: #64748B;");
        }
    }

    private void startTimeUpdater() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }

        updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Platform.runLater(this::updateTimeRemaining);
                }));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    @FXML
    public void handleClick() {
        if (onAuctionSelected != null && auction != null) {
            onAuctionSelected.accept(auction);
        }
    }
}
