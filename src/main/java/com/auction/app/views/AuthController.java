package com.auction.app.views;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class AuthController {

    @FXML private StackPane authBox;
    @FXML private VBox brandingSection;
    @FXML private StackPane formSection;
    @FXML private VBox loginContainer;
    @FXML private VBox registerContainer;
    @FXML private AnchorPane introLayer;
    @FXML private StackPane subSceneContainer;

    @FXML private TextField registerUsernameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private Label registerErrorLabel;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;

    private Group scale3DGroup;
    private PerspectiveCamera camera;

    @FXML
    public void initialize() {
        setup3DScene();
        playIntroAnimation();
    }

    private void setup3DScene() {
        scale3DGroup = build3DScale();

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateZ(-1000);
        pointLight.setTranslateY(-400);
        pointLight.setTranslateX(400);

        AmbientLight ambientLight = new AmbientLight(Color.rgb(150, 150, 150));

        Group root3D = new Group(scale3DGroup, pointLight, ambientLight);
        SubScene subScene = new SubScene(root3D, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-500);
        subScene.setCamera(camera);

        subSceneContainer.getChildren().add(subScene);
    }

    private Group build3DScale() {
        PhongMaterial goldMaterial = new PhongMaterial();
        goldMaterial.setDiffuseColor(Color.GOLD);
        goldMaterial.setSpecularColor(Color.WHITE);
        goldMaterial.setSpecularPower(80);

        Cylinder base = new Cylinder(100, 35);
        base.setMaterial(goldMaterial);
        base.setTranslateY(200);

        Sphere baseDome = new Sphere(95);
        baseDome.setMaterial(goldMaterial);
        baseDome.setScaleY(0.5);
        baseDome.setTranslateY(175);

        Cylinder pole = new Cylinder(22, 350);
        pole.setMaterial(goldMaterial);

        Cylinder arm = new Cylinder(16, 450);
        arm.setMaterial(goldMaterial);
        arm.setRotationAxis(Rotate.Z_AXIS);
        arm.setRotate(90);
        arm.setTranslateY(-150);

        Sphere centerCap = new Sphere(30);
        centerCap.setMaterial(goldMaterial);
        centerCap.setTranslateY(-150);

        Group leftPan = createPan(goldMaterial);
        leftPan.setTranslateX(-210);
        leftPan.setTranslateY(-40);

        Group rightPan = createPan(goldMaterial);
        rightPan.setTranslateX(210);
        rightPan.setTranslateY(-40);

        return new Group(base, baseDome, pole, arm, centerCap, leftPan, rightPan);
    }

    private Group createPan(PhongMaterial material) {
        Sphere bowl = new Sphere(85);
        bowl.setMaterial(material);
        bowl.setScaleY(0.35);
        bowl.setTranslateY(120);

        Cylinder string1 = new Cylinder(2.5, 130);
        string1.setMaterial(material);
        string1.setTranslateX(60);
        string1.setTranslateY(60);
        string1.setRotationAxis(Rotate.Z_AXIS);
        string1.setRotate(25);

        Cylinder string2 = new Cylinder(2.5, 130);
        string2.setMaterial(material);
        string2.setTranslateX(-60);
        string2.setTranslateY(60);
        string2.setRotationAxis(Rotate.Z_AXIS);
        string2.setRotate(-25);

        return new Group(bowl, string1, string2);
    }

    private void playIntroAnimation() {
        scale3DGroup.setTranslateX(400);
        scale3DGroup.setTranslateY(300);
        scale3DGroup.setTranslateZ(-100);
        scale3DGroup.setRotationAxis(Rotate.Y_AXIS);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(7), scale3DGroup);
        scale.setFromX(2.0);
        scale.setFromY(2.0);
        scale.setFromZ(2.0);
        scale.setToX(0.45);
        scale.setToY(0.45);
        scale.setToZ(0.45);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition translate = new TranslateTransition(Duration.seconds(7), scale3DGroup);
        translate.setToX(0);
        translate.setToY(0);
        translate.setToZ(1500);
        translate.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition rotate = new RotateTransition(Duration.seconds(7), scale3DGroup);
        rotate.setByAngle(1440);
        rotate.setInterpolator(Interpolator.LINEAR);

        ParallelTransition intro = new ParallelTransition(scale, translate, rotate);
        intro.setOnFinished(e -> playShowBoxAnimation());
        intro.play();
    }

    private void playShowBoxAnimation() {
        FadeTransition fadeIntro = new FadeTransition(Duration.seconds(1.5), introLayer);
        fadeIntro.setToValue(0);

        FadeTransition fadeAuth = new FadeTransition(Duration.seconds(2), authBox);
        fadeAuth.setToValue(1);

        TranslateTransition slideAuth = new TranslateTransition(Duration.seconds(2), authBox);
        slideAuth.setToY(0);

        ParallelTransition transition = new ParallelTransition(fadeIntro, fadeAuth, slideAuth);
        transition.setOnFinished(e -> introLayer.setVisible(false));
        transition.play();
    }

    private void playSwapAnimation(boolean toRegister) {
        TranslateTransition brandSlide = new TranslateTransition(Duration.seconds(0.8), brandingSection);
        brandSlide.setToX(toRegister ? 200 : -200);

        TranslateTransition formSlide = new TranslateTransition(Duration.seconds(0.8), formSection);
        formSlide.setToX(toRegister ? -200 : 200);

        PauseTransition toggleVisibility = new PauseTransition(Duration.seconds(0.4));
        toggleVisibility.setOnFinished(e -> {
            loginContainer.setVisible(!toRegister);
            registerContainer.setVisible(toRegister);
        });

        new ParallelTransition(brandSlide, formSlide, toggleVisibility).play();
    }

    @FXML private void goToRegister(ActionEvent event) { playSwapAnimation(true); }
    @FXML private void goToLogin(ActionEvent event) { playSwapAnimation(false); }
    @FXML private void handleLogin(ActionEvent event) { }
    @FXML private void handleRegister(ActionEvent event) { }
}