package com.auction.app.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.auction.app.domains.auth.AuthResponse;
import com.auction.app.infrastructure.session.UserSession;

import java.io.IOException;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class HomeController {

    private final ApplicationContext springContext;
    private final UserSession userSession;

    @FXML
    private Label welcomeLabel;

    private AuthResponse userInfo;

    public void setUserInfo(AuthResponse authResponse) {
        this.userInfo = authResponse;
        if (welcomeLabel != null && authResponse != null) {
            welcomeLabel.setText("Welcome back, " + authResponse.getUsername() + "!");
        }
    }

    @FXML
    public void goToProfile() {
        try {
            // Load UserView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserView.fxml"));

            // Use Spring Context for dependency injection in UserView controller
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            // Switch Scene
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Profile — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load UserView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Still keeping this just in case, though I'll add one to the User Profile too
    @FXML
    public void handleLogout() {
        try {
            // Clear session
            userSession.clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("BidVault — Authentication");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToCreateAuction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateAuctionView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Create Auction — BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load CreateAuctionView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}