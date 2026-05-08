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
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ProductSearchBarComponent extends HBox {

    private final ApplicationContext springContext;

    @FXML
    private TextField searchField;

    private Consumer<String> onSearch;
    private PauseTransition debounceTimer;

    public ProductSearchBarComponent() {
        this(null);
    }

    public void setOnSearch(Consumer<String> handler) {
        this.onSearch = handler;
    }

    @FXML
    public void initialize() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                debounceSearch(newVal);
            });
        }
    }

    private void debounceSearch(String query) {
        if (debounceTimer != null) {
            debounceTimer.stop();
        }

        debounceTimer = new PauseTransition(Duration.millis(300));
        debounceTimer.setOnFinished(event -> {
            if (onSearch != null) {
                Platform.runLater(() -> onSearch.accept(query));
            }
        });
        debounceTimer.play();
    }
}
