package com.auction.app.views;

import com.auction.app.domains.product.ProductRequest;
import com.auction.app.domains.product.ProductResponse;
import com.auction.app.domains.product.ProductService;
import com.auction.app.domains.tag.Tag;
import com.auction.app.domains.tag.TagRepository;
import com.auction.app.infrastructure.session.UserSession;
import com.auction.app.views.components.ProductItemComponent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class InventoryController {

    private final ApplicationContext springContext;
    private final ProductService productService;
    private final TagRepository tagRepository;
    private final UserSession userSession;

    @FXML
    private FlowPane inventoryFlow;
    @FXML
    private Button addSamplesButton;
    @FXML
    private javafx.scene.control.Label debugLabel;

    @FXML
    public void initialize() {
        loadInventory();
    }

    private void loadInventory() {
        new Thread(() -> {
            try {
                String email = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getEmail() : null;
                System.err.println("[Inventory] resolved session email=" + email);
                if (email == null) {
                    Platform.runLater(() -> {
                        if (debugLabel != null) debugLabel.setText("Not logged in");
                    });
                    return;
                }
                List<ProductResponse> products = productService.searchUserProducts(email, null, null);
                System.err.println("[Inventory] fetched products count=" + (products == null ? 0 : products.size()));
                Platform.runLater(() -> {
                    if (debugLabel != null) debugLabel.setText("user=" + email + " • items=" + (products == null ? 0 : products.size()));
                    populateInventory(products);
                });
            } catch (Exception e) {
                System.err.println("Failed to load inventory: " + e.getMessage());
            }
        }).start();
    }

    private void populateInventory(List<ProductResponse> products) {
        inventoryFlow.getChildren().clear();
        for (ProductResponse product : products) {
            try {
                // Use Spring-managed component instance as root/controller for fx:root FXML
                ProductItemComponent controller = springContext.getBean(ProductItemComponent.class);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ProductItem.fxml"));
                loader.setRoot(controller);
                loader.setController(controller);
                loader.load();
                VBox node = controller;
                controller.setProduct(product);
                controller.setOnProductSelected(p -> openCreateAuctionWithProduct(p));
                inventoryFlow.getChildren().add(node);
            } catch (IOException ex) {
                System.err.println("Failed to load product item: " + ex.getMessage());
            }
        }
    }

    private void openCreateAuctionWithProduct(ProductResponse product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateAuctionView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            CreateAuctionViewController controller = loader.getController();
            controller.setSelectedProduct(product);
            Stage stage = (Stage) inventoryFlow.getScene().getWindow();
            stage.setTitle("Create Auction — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to open CreateAuctionView: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddSamples() {
        // Open modal dialog to select samples
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddSamplesDialog.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initOwner(inventoryFlow.getScene().getWindow());
            dialog.setTitle("Add Sample Items");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            AddSamplesController ctrl = loader.getController();
            List<AddSamplesController.Selection> selections = ctrl.getSelections();
            if (selections.isEmpty()) return;

            String email = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getEmail() : null;
            if (email == null) return;

            // Persist selected items
            for (AddSamplesController.Selection s : selections) {
                Long tagId = ensureTag(guessTagFromName(s.getName()));
                ProductRequest req = new ProductRequest();
                req.setProductName(s.getName());
                req.setPrice(BigDecimal.valueOf(9.99));
                req.setQuantity(s.getQuantity());
                req.setTagIds(List.of(tagId));
                productService.addProduct(email, req);
            }

            loadInventory();
        } catch (IOException e) {
            System.err.println("Failed to open Add Samples dialog: " + e.getMessage());
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) inventoryFlow.getScene().getWindow();
            stage.setTitle("Home — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to go back to Home: " + e.getMessage());
        }
    }

    private String guessTagFromName(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("headphone") || lower.contains("phone") || lower.contains("elect")) return "Electronics";
        if (lower.contains("coin") || lower.contains("vintage") || lower.contains("collect")) return "Collectibles";
        if (lower.contains("book")) return "Books";
        return "Collectibles";
    }

    private Long ensureTag(String name) {
        Optional<Tag> existing = tagRepository.findAll().stream()
                .filter(t -> t.getTagName().equalsIgnoreCase(name))
                .findFirst();
        if (existing.isPresent()) return existing.get().getTagId();

        Tag tag = new Tag();
        tag.setTagName(name);
        Tag saved = tagRepository.save(tag);
        return saved.getTagId();
    }
}
