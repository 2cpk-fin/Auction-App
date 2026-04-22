package com.auction.app.view;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// This tells Spring to manage this class
@Component
public class MainController {

    @FXML
    private Label statusLabel; // Match the fx:id in your FXML

    // You can @Autowired your Auction Services here later!

    @FXML
    public void initialize() {
        // This runs when the UI loads, like useEffect(() => {}, [])
        statusLabel.setText("Auction System Ready!");
    }

    @FXML
    public void handleButtonClick() {
        System.out.println("Button clicked!");
    }
}