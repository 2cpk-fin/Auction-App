package com.auction.app.views;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class AddSamplesController {

    @FXML
    private VBox itemsContainer;

    // simple in-memory sample list; InventoryController will read selections after dialog close
    @Getter
    private final List<Selection> selections = new ArrayList<>();

    private final Map<CheckBox, Spinner<Integer>> spinnerMap = new HashMap<>();

    @FXML
    public void initialize() {
        // default samples
        addItemRow("Sample Headphones", 1);
        addItemRow("Vintage Coin", 1);
        addItemRow("Hardcover Book", 1);
    }

    private void addItemRow(String name, int defaultQty) {
        CheckBox cb = new CheckBox(name);
        Spinner<Integer> sp = new Spinner<>();
        sp.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, defaultQty));
        sp.setPrefWidth(80);
        VBox row = new VBox();
        row.setSpacing(4);
        row.getChildren().addAll(cb, sp);
        itemsContainer.getChildren().add(row);
        spinnerMap.put(cb, sp);
    }

    @FXML
    public void handleAdd() {
        selections.clear();
        for (Map.Entry<CheckBox, Spinner<Integer>> e : spinnerMap.entrySet()) {
            if (e.getKey().isSelected()) {
                selections.add(new Selection(e.getKey().getText(), e.getValue().getValue()));
            }
        }
        close();
    }

    @FXML
    public void handleCancel() {
        selections.clear();
        close();
    }

    private void close() {
        Stage stage = (Stage) itemsContainer.getScene().getWindow();
        stage.close();
    }

    public static class Selection {
        private final String name;
        private final int quantity;

        public Selection(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
