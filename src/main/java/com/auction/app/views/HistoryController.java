package com.auction.app.views;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.auction.app.domains.history.AuctionHistoryEventResponse;
import com.auction.app.domains.history.AuctionHistoryEventType;
import com.auction.app.domains.history.AuctionHistoryService;
import com.auction.app.domains.history.AuctionPlayerStatsResponse;
import com.auction.app.infrastructure.session.UserSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoryController {

    private final AuctionHistoryService historyService;
    private final UserSession userSession;
    private final ApplicationContext springContext;

    @FXML
    private Button backButton;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label totalEarnedLabel;
    @FXML
    private Label totalRefundedLabel;
    @FXML
    private Label totalBidsPlacedLabel;
    @FXML
    private Label auctionsWonLabel;
    @FXML
    private Label auctionsSoldLabel;
    @FXML
    private Label auctionsCancelledLabel;
    @FXML
    private Label winRateLabel;

    @FXML
    private ComboBox<AuctionHistoryEventType> eventTypeFilter;
    @FXML
    private TableView<AuctionHistoryEventResponse> historyTable;
    @FXML
    private TableColumn<AuctionHistoryEventResponse, String> dateColumn;
    @FXML
    private TableColumn<AuctionHistoryEventResponse, AuctionHistoryEventType> eventColumn;
    @FXML
    private TableColumn<AuctionHistoryEventResponse, String> auctionIdColumn;
    @FXML
    private TableColumn<AuctionHistoryEventResponse, String> amountColumn;

    @FXML
    private Label emptyStateLabel;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final ObservableList<AuctionHistoryEventResponse> allEvents = FXCollections.observableArrayList();
    private final ObservableList<AuctionHistoryEventResponse> filteredEvents = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        setupEventTypeFilter();
        setupTableColumns();
        setupEmptyState();
        loadData();
    }

    private void setupEventTypeFilter() {
        // Add "All" option at the beginning
        ObservableList<AuctionHistoryEventType> filterOptions = FXCollections.observableArrayList();
        filterOptions.add(null); // null represents "All"
        filterOptions.addAll(AuctionHistoryEventType.values());

        eventTypeFilter.setItems(filterOptions);
        eventTypeFilter.getSelectionModel().selectFirst(); // Select "All" by default

        // Format the ComboBox display
        eventTypeFilter.setCellFactory(lv -> new ListCell<AuctionHistoryEventType>() {
            @Override
            protected void updateItem(AuctionHistoryEventType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Events");
                } else {
                    setText(formatEventType(item));
                }
            }
        });

        eventTypeFilter.setButtonCell(new ListCell<AuctionHistoryEventType>() {
            @Override
            protected void updateItem(AuctionHistoryEventType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Events");
                } else {
                    setText(formatEventType(item));
                }
            }
        });

        // Filter table when selection changes
        eventTypeFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterTable(newVal);
        });
    }

    private void setupTableColumns() {
        // Date column
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getOccurredAt().atZone(java.time.ZoneId.systemDefault())
                    .format(DATE_FORMATTER)
            )
        );

        // Event column with color coding
        eventColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEventType())
        );
        eventColumn.setCellFactory(col -> new TableCell<AuctionHistoryEventResponse, AuctionHistoryEventType>() {
            @Override
            protected void updateItem(AuctionHistoryEventType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(formatEventType(item));
                    setTextFill(getEventColor(item));
                }
            }
        });

        // Auction ID column
        auctionIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatAuctionId(cellData.getValue().getAuctionId())
            )
        );

        // Amount column
        amountColumn.setCellValueFactory(cellData -> {
            Long amount = cellData.getValue().getCoinAmount();
            String displayAmount = amount != null ? amount + " coins" : "—";
            return new javafx.beans.property.SimpleStringProperty(displayAmount);
        });

        historyTable.setItems(filteredEvents);
    }

    private void setupEmptyState() {
        emptyStateLabel.setVisible(false);
        loadingIndicator.setVisible(false);
    }

    private void loadData() {
        loadingIndicator.setVisible(true);
        emptyStateLabel.setVisible(false);
        historyTable.setVisible(false);

        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UUID userId = userSession.getCurrentUserId();
                if (userId == null) {
                    return null;
                }

                // Load stats
                AuctionPlayerStatsResponse stats = historyService.getPlayerStats(userId);
                
                // Load history - fetch first page
                Pageable pageable = PageRequest.of(0, 1000); // Load up to 1000 records
                Page<AuctionHistoryEventResponse> page = historyService.getPlayerHistory(userId, pageable);

                Platform.runLater(() -> {
                    updateStatsUI(stats);
                    updateHistoryTable(page.getContent());
                    loadingIndicator.setVisible(false);
                    historyTable.setVisible(true);
                });

                return null;
            }
        };

        // Handle task failure
        loadTask.setOnFailed(event -> {
            loadingIndicator.setVisible(false);
            emptyStateLabel.setText("Error loading history. Please try again.");
            emptyStateLabel.setVisible(true);
            System.err.println("Error loading history");
        });

        // Run task in background thread
        new Thread(loadTask).start();
    }

    private void updateStatsUI(AuctionPlayerStatsResponse stats) {
        if (stats == null) {
            return;
        }

        totalSpentLabel.setText(stats.getTotalCoinsSpent() != null ? stats.getTotalCoinsSpent().toString() : "0");
        totalEarnedLabel.setText(stats.getTotalCoinsEarned() != null ? stats.getTotalCoinsEarned().toString() : "0");
        totalRefundedLabel.setText(stats.getTotalCoinsRefunded() != null ? stats.getTotalCoinsRefunded().toString() : "0");
        totalBidsPlacedLabel.setText(stats.getTotalBidsPlaced() != null ? stats.getTotalBidsPlaced().toString() : "0");
        auctionsWonLabel.setText(stats.getAuctionsWon() != null ? stats.getAuctionsWon().toString() : "0");
        auctionsSoldLabel.setText(stats.getAuctionsSold() != null ? stats.getAuctionsSold().toString() : "0");
        auctionsCancelledLabel.setText(stats.getAuctionsCancelled() != null ? stats.getAuctionsCancelled().toString() : "0");

        if (stats.getWinRate() != null) {
            int winRatePercent = Math.round(stats.getWinRate().floatValue() * 100);
            winRateLabel.setText(winRatePercent + "%");
        } else {
            winRateLabel.setText("0%");
        }
    }

    private void updateHistoryTable(List<AuctionHistoryEventResponse> events) {
        allEvents.clear();
        allEvents.addAll(events);
        filterTable(eventTypeFilter.getValue());

        if (filteredEvents.isEmpty()) {
            emptyStateLabel.setVisible(true);
            historyTable.setVisible(false);
        } else {
            emptyStateLabel.setVisible(false);
            historyTable.setVisible(true);
        }
    }

    private void filterTable(AuctionHistoryEventType selectedFilter) {
        if (selectedFilter == null) {
            // Show all events
            filteredEvents.clear();
            filteredEvents.addAll(allEvents);
        } else {
            // Filter by selected event type
            filteredEvents.clear();
            List<AuctionHistoryEventResponse> filtered = allEvents.stream()
                .filter(event -> event.getEventType() == selectedFilter)
                .collect(Collectors.toList());
            filteredEvents.addAll(filtered);
        }

        // Update empty state visibility
        if (filteredEvents.isEmpty()) {
            emptyStateLabel.setVisible(true);
            historyTable.setVisible(false);
        } else {
            emptyStateLabel.setVisible(false);
            historyTable.setVisible(true);
        }

        historyTable.refresh();
    }

    private Color getEventColor(AuctionHistoryEventType eventType) {
        if (eventType == null) {
            return Color.BLACK;
        }

        return switch (eventType) {
            case AUCTION_WON, AUCTION_SOLD -> Color.web("#16A34A"); // Green
            case BID_PLACED -> Color.web("#2563EB"); // Blue
            case AUCTION_CANCELLED -> Color.web("#DC2626"); // Red
            case OUTBID, REFUNDED -> Color.web("#EA580C"); // Orange
        };
    }

    private String formatEventType(AuctionHistoryEventType eventType) {
        if (eventType == null) {
            return "All Events";
        }
        String[] words = eventType.toString().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
        }
        return result.toString().trim();
    }

    private String formatAuctionId(UUID auctionId) {
        if (auctionId == null) {
            return "—";
        }
        String id = auctionId.toString();
        return id.substring(0, Math.min(8, id.length())) + "...";
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load HomeView: " + e.getMessage());
        }
    }
}
