package org.horizonteurbano.system.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private AnchorPane apMainContainer;
    @FXML
    private ImageView imgLogo;
    
    private double angle = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/org/horizonteurbano/system/view/logoImage.png"));
            if (imgLogo != null && img != null) {
                imgLogo.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        applyCircularCrop();
        startGradientAnimation();
    }

    private void applyCircularCrop() {
        if (imgLogo != null) {
            double size = 160.0; //Dimensión cuadrada para el logo
            imgLogo.setFitWidth(size);
            imgLogo.setFitHeight(size);

            Circle clip = new Circle(size / 2.0, size / 2.0, size / 2.0);
            imgLogo.setClip(clip);
        }
    }

    private void startGradientAnimation() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(35), e -> {
            angle = (angle + 0.4) % 360;

            double startX = 50 + 50 * Math.cos(Math.toRadians(angle));
            double startY = 50 + 50 * Math.sin(Math.toRadians(angle));
            double endX = 50 - 50 * Math.cos(Math.toRadians(angle));
            double endY = 50 - 50 * Math.sin(Math.toRadians(angle));

            String cssGradiente = String.format(
                    "-fx-background-color: linear-gradient(from %.1f%% %.1f%% to %.1f%% %.1f%%, #E3D8C8 0%%, #A4AD8F 50%%, #6E8354 100%%);",
                    startX, startY, endX, endY
            );

            if (apMainContainer != null) {
                apMainContainer.setStyle(cssGradiente);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
