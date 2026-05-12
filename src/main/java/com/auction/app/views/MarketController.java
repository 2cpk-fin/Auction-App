package com.auction.app.views;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.auction.app.domains.auction.auction.AuctionResponse;
import com.auction.app.domains.auction.auction.AuctionService;
import com.auction.app.views.components.AuctionItemComponent;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MarketController {

    private final ApplicationContext springContext;
    private final AuctionService auctionService;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private ComboBox<String> auctionTypeCombo;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private Button searchButton;
    @FXML
    private FlowPane resultsFlow;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageInfoLabel;

    private int currentPage = 0;
    private final int pageSize = 12;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        setupAuctionTypeCombo();
        setupCategoryCombo();
        setupNumericFilter(minPriceField);
        setupNumericFilter(maxPriceField);
        auctionTypeCombo.setValue("ALL");
        // Initial load
        performSearch();
    }

    private void setupAuctionTypeCombo() {
        auctionTypeCombo.getItems().addAll("ALL", "BIN", "AUCTION");
    }

    private void setupCategoryCombo() {
        // For now, populate with a few categories. In a full app this should come from the backend.
        categoryCombo.getItems().addAll("", "Electronics", "Collectibles", "Books", "Clothing");
        categoryCombo.setValue("");
    }

    private void setupNumericFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            if (!newVal.matches("\\d*")) {
                field.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    public void handleSearch() {
        currentPage = 0;
        performSearch();
    }

    @FXML
    public void handlePrev() {
        if (currentPage > 0) {
            currentPage--;
            performSearch();
        }
    }

    @FXML
    public void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            performSearch();
        }
    }

    private void performSearch() {
        String category = emptyToNull(categoryCombo.getValue());
        String auctionType = emptyToNull(auctionTypeCombo.getValue());
        if (Objects.equals(auctionType, "ALL")) {
            auctionType = null;
        }

        Long minPrice = parseLongOrNull(minPriceField.getText());
        Long maxPrice = parseLongOrNull(maxPriceField.getText());
        String itemName = emptyToNull(searchField.getText());

        final String finalCategory = category;
        final String finalAuctionType = auctionType;
        final Long finalMin = minPrice;
        final Long finalMax = maxPrice;
        final String finalName = itemName;

        // disable controls while search runs
        Platform.runLater(() -> {
            if (searchButton != null) searchButton.setDisable(true);
            if (prevButton != null) prevButton.setDisable(true);
            if (nextButton != null) nextButton.setDisable(true);
        });

        Task<Page<AuctionResponse>> task = new Task<>() {
            @Override
            protected Page<AuctionResponse> call() {
                Pageable pageable = PageRequest.of(currentPage, pageSize);
                return auctionService.searchAuctions(finalCategory, finalAuctionType, finalMin, finalMax, finalName, pageable);
            }
        };

        task.setOnSucceeded(evt -> {
            Page<AuctionResponse> page = task.getValue();
            if (page == null) {
                updateResults(List.of());
                return;
            }
            totalPages = page.getTotalPages() == 0 ? 1 : page.getTotalPages();
            updateResults(page.getContent());
            Platform.runLater(() -> pageInfoLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages)));
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            System.err.println("Market search failed: " + (ex == null ? "unknown" : ex.getMessage()));
        });

        task.setOnSucceeded(evt -> updateNavigationState());
        task.setOnFailed(evt -> updateNavigationState());

        new Thread(task).start();
    }

    private void updateResults(List<AuctionResponse> auctions) {
        Platform.runLater(() -> {
            resultsFlow.getChildren().clear();
            for (AuctionResponse auction : auctions) {
                try {
                    AuctionItemComponent controller = springContext.getBean(AuctionItemComponent.class);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/AuctionItem.fxml"));
                    loader.setRoot(controller);
                    loader.setController(controller);
                    loader.load();
                    Pane node = controller;
                    controller.setAuction(auction);
                    controller.setOnAuctionSelected(a -> {
                        // Placeholder: navigate to auction details in future
                    });
                    resultsFlow.getChildren().add(node);
                } catch (IOException e) {
                    System.err.println("Failed to load auction item component: " + e.getMessage());
                }
            }
            // update navigation buttons after populating
            updateNavigationState();
        });
    }

    private Long parseLongOrNull(String text) {
        if (text == null) return null;
        if (text.trim().isEmpty()) return null;
        try {
            return Long.valueOf(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateNavigationState() {
        Platform.runLater(() -> {
            if (prevButton != null) prevButton.setDisable(currentPage <= 0);
            if (nextButton != null) nextButton.setDisable(currentPage >= totalPages - 1);
            if (searchButton != null) searchButton.setDisable(false);
            if (pageInfoLabel != null) pageInfoLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
        });
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Pane root = loader.load();
            Stage stage = (Stage) resultsFlow.getScene().getWindow();
            stage.setTitle("Home — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to navigate back to Home: " + e.getMessage());
        }
    }
}
