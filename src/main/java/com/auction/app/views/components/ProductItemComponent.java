package com.auction.app.views.components;

import com.auction.app.domains.product.ProductResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ProductItemComponent extends VBox {

    private final ApplicationContext springContext;

    @FXML
    private Label productNameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label quantityLabel;

    private ProductResponse product;
    private Consumer<ProductResponse> onProductSelected;
    private boolean isSelected = false;

    public ProductItemComponent() {
        this(null);
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
        updateUI();
    }

    public void setOnProductSelected(Consumer<ProductResponse> handler) {
        this.onProductSelected = handler;
    }

    private void updateUI() {
        if (product == null) return;

        if (productNameLabel != null) {
            productNameLabel.setText(product.getProductName());
        }
        if (priceLabel != null) {
            priceLabel.setText("$" + product.getPrice());
        }
        if (quantityLabel != null) {
            quantityLabel.setText("Qty: " + product.getQuantity());
        }
    }

    @FXML
    public void handleClick() {
        toggleSelection();
        if (onProductSelected != null && product != null) {
            onProductSelected.accept(product);
        }
    }

    public void toggleSelection() {
        isSelected = !isSelected;
        if (isSelected) {
            setStyle(getStyle() + "; -fx-border-color: #4F46E5; -fx-border-width: 2;");
        } else {
            setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-padding: 15;");
        }
    }

    public void deselect() {
        isSelected = false;
        setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-padding: 15;");
    }
}
