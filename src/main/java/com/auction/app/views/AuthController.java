package com.auction.app.views;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.auction.app.domains.auth.AuthResponse;
import com.auction.app.domains.auth.AuthService;
import com.auction.app.domains.auth.LoginRequest;
import com.auction.app.domains.auth.RegisterRequest;
import com.auction.app.infrastructure.session.UserSession;

import java.io.IOException;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApplicationContext springContext;
    private final UserSession userSession;

    // ── Login fields ──────────────────────────────────────────────────────────
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginErrorLabel;
    @FXML
    private Button loginButton;

    // ── Register fields ───────────────────────────────────────────────────────
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private Label registerErrorLabel;
    @FXML
    private Button registerButton;

    // ── Mobile Animation Containers ───────────────────────────────────────────
    @FXML
    private VBox loginContainer;
    @FXML
    private VBox registerContainer;

    // =========================================================================
    // LOGIN
    // =========================================================================

    @FXML
    public void handleLogin() {
        clearError(loginErrorLabel);

        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError(loginErrorLabel, "Email and password are required.");
            return;
        }

        // Relaxed validation so you can test locally without getting blocked
        if (!email.contains("@")) {
            showError(loginErrorLabel, "Please enter a valid email address containing '@'.");
            return;
        }

        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);

            AuthResponse response = authService.loginUser(request);

            // Save to UserSession
            userSession.setAuthResponse(response);

            navigateTo("/fxml/HomeView.fxml", "Home — BidVault", response);

        } catch (RuntimeException e) {
            showError(loginErrorLabel, "Login failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // REGISTER
    // =========================================================================

    @FXML
    public void handleRegister() {
        clearError(registerErrorLabel);

        String username = registerUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(registerErrorLabel, "All fields are required.");
            return;
        }
        if (!email.contains("@")) {
            showError(registerErrorLabel, "Please enter a valid email address containing '@'.");
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            showError(registerErrorLabel, "Password must be between 6 and 16 characters.");
            return;
        }

        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);

            authService.registerUser(request);

            // Successfully registered, slide back to login
            goToLogin();
            showError(loginErrorLabel, "Registration successful! Please sign in.");
            loginErrorLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12;"); // Green success

        } catch (RuntimeException e) {
            showError(registerErrorLabel, "Registration failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // MOBILE SLIDE ANIMATIONS
    // =========================================================================

    @FXML
    public void goToRegister() {
        clearError(loginErrorLabel);
        clearError(registerErrorLabel);

        // Slide Login off to the left (-400px)
        TranslateTransition slideLogin = new TranslateTransition(Duration.millis(350), loginContainer);
        slideLogin.setToX(-400);
        slideLogin.play();

        // Slide Register in from the right (to 0px)
        TranslateTransition slideRegister = new TranslateTransition(Duration.millis(350), registerContainer);
        slideRegister.setToX(0);
        slideRegister.play();
    }

    @FXML
    public void goToLogin() {
        clearError(loginErrorLabel);
        clearError(registerErrorLabel);
        loginErrorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12;"); // Reset to red error

        // Slide Register off to the right (400px)
        TranslateTransition slideRegister = new TranslateTransition(Duration.millis(350), registerContainer);
        slideRegister.setToX(400);
        slideRegister.play();

        // Slide Login in from the left (to 0px)
        TranslateTransition slideLogin = new TranslateTransition(Duration.millis(350), loginContainer);
        slideLogin.setToX(0);
        slideLogin.play();
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private void navigateTo(String fxmlPath, String title, AuthResponse authData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Object nextController = loader.getController();
            if (nextController instanceof HomeController hc && authData != null) {
                hc.setUserInfo(authData);
            }

            Stage stage = getCurrentStage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage getCurrentStage() {
        if (loginButton != null)
            return (Stage) loginButton.getScene().getWindow();
        if (registerButton != null)
            return (Stage) registerButton.getScene().getWindow();
        throw new IllegalStateException("Cannot resolve current stage — no FXML field available.");
    }

    private void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
        }
    }

    private void clearError(Label label) {
        if (label != null) {
            label.setText("");
            label.setVisible(false);
        }
    }
}