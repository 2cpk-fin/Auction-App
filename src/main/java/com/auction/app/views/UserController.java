package com.auction.app.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.auction.app.domains.user.UserResponse;
import com.auction.app.domains.user.UserServiceImpl;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final ApplicationContext springContext;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    // TODO: Replace with actual session logic later
    private final Long loggedInUserId = 1L;

    @FXML
    public void initialize() {
        try {
            UserResponse user = userService.getUserById(loggedInUserId);
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
        } catch (Exception e) {
            showError("Could not load profile: " + e.getMessage());
        }
    }

    @FXML
    public void onUpdateUsername() {
        try {
            String newUsername = usernameField.getText();
            UserResponse response = userService.updateUsername(loggedInUserId, newUsername);
            showSuccess("Username updated successfully.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void onUpdateEmail() {
        try {
            String newEmail = emailField.getText();
            UserResponse response = userService.updateEmail(loggedInUserId, newEmail);
            showSuccess("Email updated successfully.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void onUpdatePassword() {
        try {
            String newPass = passwordField.getText();
            userService.updatePassword(loggedInUserId, newPass);
            showSuccess("Password changed successfully!");
            passwordField.clear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void onDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete account? This cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.deleteUserById(loggedInUserId);
                showSuccess("Account deleted.");
                handleLogout(); // Kick them out to AuthView after deletion
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // ── NAVIGATION METHODS ──────────────────────────────────────────────────

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("BidVault");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setTitle("BidVault — Authentication");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── UI HELPERS ──────────────────────────────────────────────────────────

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.web("#4F46E5"));
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.web("#EF4444"));
    }
}