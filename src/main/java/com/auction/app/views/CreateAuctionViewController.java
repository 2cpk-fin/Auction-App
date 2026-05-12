package com.auction.app.views;

import com.auction.app.domains.auction.auction.AuctionResponse;
import com.auction.app.domains.auction.auction.AuctionType;
import com.auction.app.domains.product.ProductResponse;
import com.auction.app.infrastructure.client.ApiClient;
import com.auction.app.domains.product.ProductService;
import com.auction.app.infrastructure.session.UserSession;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.auction.app.views.components.ProductItemComponent;
import com.auction.app.views.components.AuctionItemComponent;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.UUID;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CreateAuctionViewController {

    private final ApplicationContext springContext;
    private final ApiClient apiClient;
    private final UserSession userSession;
    private final ProductService productService;

    // Search section
    @FXML
    private TextField searchField;

    // Active auctions section
    @FXML
    private ScrollPane auctionsScrollPane;
    @FXML
    private HBox auctionsContainer;

    // Product grid section
    @FXML
    private FlowPane productGrid;

    // Right panel for auction creation form
    @FXML
    private VBox formPanel;
    @FXML
    private Label selectedProductNameLabel;
    @FXML
    private ComboBox<AuctionType> auctionTypeCombo;
    @FXML
    private TextField binPriceField;
    @FXML
    private TextField startingBidField;
    @FXML
    private ComboBox<String> durationCombo;
    @FXML
    private TextField categoryField;
    @FXML
    private Button submitButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private ProductResponse selectedProduct;
    private PauseTransition searchDebounce;

    @FXML
    public void initialize() {
        setupAuctionTypeCombo();
        setupDurationCombo();
        setupSearchDebounce();
        hideFormPanel();
        loadData();
    }

    private void setupAuctionTypeCombo() {
        auctionTypeCombo.getItems().addAll(AuctionType.values());
        auctionTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFormFieldsVisibility();
        });
    }

    private void setupDurationCombo() {
        durationCombo.getItems().addAll("1h", "6h", "12h", "24h", "48h");
        durationCombo.setValue("24h");
    }

    private void setupSearchDebounce() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchDebounce != null) {
                searchDebounce.stop();
            }
            searchDebounce = new PauseTransition(Duration.millis(300));
            searchDebounce.setOnFinished(event -> searchProducts(newVal));
            searchDebounce.play();
        });
    }

    private void loadData() {
        loadActiveAuctions();
        loadAllProducts();
    }

    private void loadActiveAuctions() {
        showLoading(true);
        new Thread(() -> {
            try {
                UUID sellerId = userSession.getCurrentUserId();
                String endpoint = "/api/auctions/seller/" + sellerId;
                // Note: API endpoint may vary; adjust based on actual REST API
                List<AuctionResponse> auctions = apiClient.get(endpoint, new TypeReference<List<AuctionResponse>>() {}, userSession.getToken());
                Platform.runLater(() -> {
                    populateAuctionsStrip(auctions != null ? auctions : List.of());
                    showLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load auctions: " + e.getMessage());
                    showLoading(false);
                });
            }
        }).start();
    }

    private void loadAllProducts() {
        searchProducts("");
    }

    private void searchProducts(String query) {
        showLoading(true);
        new Thread(() -> {
            try {
                String email = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getEmail() : null;
                final List<ProductResponse> products = email != null
                        ? productService.searchUserProducts(email, query == null || query.isEmpty() ? null : query, null)
                        : List.of();
                Platform.runLater(() -> {
                    populateProductGrid(products);
                    showLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load products: " + e.getMessage());
                    showLoading(false);
                });
            }
        }).start();
    }

    private void populateAuctionsStrip(List<?> auctions) {
        auctionsContainer.getChildren().clear();
        for (Object obj : auctions) {
            AuctionResponse auction = (AuctionResponse) obj;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/AuctionItem.fxml"));
                loader.setControllerFactory(springContext::getBean);
                HBox auctionItem = loader.load();

                AuctionItemComponent controller = loader.getController();
                controller.setAuction(auction);

                auctionsContainer.getChildren().add(auctionItem);
                HBox.setMargin(auctionItem, new Insets(0, 10, 0, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateProductGrid(List<?> products) {
        productGrid.getChildren().clear();
        for (Object obj : products) {
            ProductResponse product = (ProductResponse) obj;
            try {
                ProductItemComponent controller = springContext.getBean(ProductItemComponent.class);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ProductItem.fxml"));
                loader.setRoot(controller);
                loader.setController(controller);
                loader.load();

                VBox productItem = controller;

                controller.setProduct(product);
                controller.setOnProductSelected(selected -> {
                    selectProduct(selected);
                    // Deselect other products
                    for (javafx.scene.Node node : productGrid.getChildren()) {
                        if (node != productItem && node instanceof VBox) {
                            // Find the controller for other items and deselect
                        }
                    }
                });

                productGrid.getChildren().add(productItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectProduct(ProductResponse product) {
        this.selectedProduct = product;
        showFormPanel();
        updateFormWithProduct();
    }

    // Public method to allow other controllers to preselect a product
    public void setSelectedProduct(ProductResponse product) {
        selectProduct(product);
    }

    private void updateFormWithProduct() {
        if (selectedProduct != null) {
            selectedProductNameLabel.setText(selectedProduct.getProductName());
            categoryField.setText(selectedProduct.getProductName()); // Adjust field name as needed
        }
    }

    private void updateFormFieldsVisibility() {
        if (auctionTypeCombo.getValue() == AuctionType.BIN) {
            binPriceField.setVisible(true);
            binPriceField.setManaged(true);
            startingBidField.setVisible(false);
            startingBidField.setManaged(false);
        } else {
            binPriceField.setVisible(false);
            binPriceField.setManaged(false);
            startingBidField.setVisible(true);
            startingBidField.setManaged(true);
        }
    }

    @FXML
    public void handleSubmit() {
        if (selectedProduct == null) {
            showError("Please select a product first");
            return;
        }

        if (auctionTypeCombo.getValue() == null) {
            showError("Please select an auction type");
            return;
        }

        if (durationCombo.getValue() == null) {
            showError("Please select a duration");
            return;
        }

        AuctionType type = auctionTypeCombo.getValue();
        Long startingBid = null;
        Long binPrice = null;

        try {
            if (type == AuctionType.BIN) {
                if (binPriceField.getText().isEmpty()) {
                    showError("Please enter a BIN price");
                    return;
                }
                binPrice = Long.parseLong(binPriceField.getText());
            } else {
                if (startingBidField.getText().isEmpty()) {
                    showError("Please enter a starting bid");
                    return;
                }
                startingBid = Long.parseLong(startingBidField.getText());
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid prices");
            return;
        }

        submitAuction(type, startingBid, binPrice);
    }

    private void submitAuction(AuctionType type, Long startingBid, Long binPrice) {
        showLoading(true);
        new Thread(() -> {
            try {
                // Prepare AuctionRequest
                // Note: Adjust the endpoint and request format based on actual API
                String endpoint = "/api/auctions";
                // Create request object and post...
                Platform.runLater(() -> {
                    showSuccess("Auction created successfully!");
                    showLoading(false);
                    loadActiveAuctions();
                    hideFormPanel();
                    clearForm();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to create auction: " + e.getMessage());
                    showLoading(false);
                });
            }
        }).start();
    }

    private void clearForm() {
        selectedProduct = null;
        selectedProductNameLabel.setText("");
        auctionTypeCombo.setValue(null);
        binPriceField.clear();
        startingBidField.clear();
        durationCombo.setValue("24h");
        categoryField.clear();
    }

    private void showFormPanel() {
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void hideFormPanel() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
            loadingIndicator.setManaged(show);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) formPanel.getScene().getWindow();
            stage.setTitle("Home — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
