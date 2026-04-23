package com.auction.app.domains.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // These fx:id names must match exactly what you put in Scene Builder / FXML
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField profilePicField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // TODO: Replace with actual session logic later
    private final Long loggedInUserId = 1L;

    /**
     * This method runs automatically when the FXML is loaded.
     * Use it to fetch the user data so the fields aren't empty.
     */
    @FXML
    public void initialize() {
        try {
            UserResponse user = userService.getUserById(loggedInUserId);
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            profilePicField.setText(user.getProfilePicture());
        } catch (Exception e) {
            showError("Could not load profile: " + e.getMessage());
        }
    }

    // Called by "Save Username" button
    @FXML
    public void onUpdateUsername() {
        try {
            String newUsername = usernameField.getText();
            UserResponse response = userService.updateUsername(loggedInUserId, newUsername);
            showSuccess("Username updated to: " + response.getUsername());
        }
        catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // Called by the "Save Email" button in FXML
    @FXML
    public void onUpdateEmail() {
        try {
            String newEmail = emailField.getText();
            UserResponse response = userService.updateEmail(loggedInUserId, newEmail);
            showSuccess("Email updated to: " + response.getEmail());
        }
        catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // Called by the "Update Picture" button in FXML
    @FXML
    public void onUpdateProfilePicture() {
        try {
            String url = profilePicField.getText();
            userService.updateProfilePicture(loggedInUserId, url);
            showSuccess("Profile picture updated!");
        }
        catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // Called by the "Change Password" button in FXML
    @FXML
    public void onUpdatePassword() {
        try {
            String newPass = passwordField.getText();
            userService.updatePassword(loggedInUserId, newPass);
            showSuccess("Password changed successfully!");
            passwordField.clear();
        }
        catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // Called by a "Delete Account" button
    @FXML
    public void onDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete account? This cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.deleteUserById(loggedInUserId);
                showSuccess("Account deleted.");
                // Redirect logic would go here
            }
            catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // Helper UI Methods
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.GREEN);
    }

    private void showError(String message) {
        messageLabel.setText("Error: " + message);
        messageLabel.setTextFill(Color.RED);
    }
}