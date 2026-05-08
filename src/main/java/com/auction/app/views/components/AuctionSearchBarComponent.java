package com.auction.app.views.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class AuctionSearchBarComponent extends HBox {

    private final ApplicationContext springContext;

    @FXML
    private TextField searchField;
    @FXML
    private javafx.scene.control.ComboBox<String> typeFilter;

    private BiConsumer<String, String> onSearch;
    private PauseTransition debounceTimer;

    public AuctionSearchBarComponent() {
        this(null);
    }

    public void setOnSearch(BiConsumer<String, String> handler) {
        this.onSearch = handler;
    }

    @FXML
    public void initialize() {
        if (typeFilter != null) {
            typeFilter.getItems().addAll("ALL", "BIN", "AUCTION");
            typeFilter.setValue("ALL");
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                debounceSearch();
            });
        }

        if (typeFilter != null) {
            typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
                debounceSearch();
            });
        }
    }

    private void debounceSearch() {
        if (debounceTimer != null) {
            debounceTimer.stop();
        }

        debounceTimer = new PauseTransition(Duration.millis(300));
        debounceTimer.setOnFinished(event -> {
            if (onSearch != null) {
                String query = searchField != null ? searchField.getText() : "";
                String type = typeFilter != null ? typeFilter.getValue() : "ALL";
                Platform.runLater(() -> onSearch.accept(query, type));
            }
        });
        debounceTimer.play();
    }
}
