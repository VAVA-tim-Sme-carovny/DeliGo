package com.deligo.Frontend.Controllers.Popups;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class StatusPopupController {

    @FXML private AnchorPane popupRoot;
    @FXML private Label popupMessage;

    public void showMessage(String message, int statusCode) {
        popupMessage.setText(message);

        // Set up a background color based on the status code
        String bgColor = switch (statusCode) {
            case 200 -> "#4CAF50";
            case 500 -> "#F44336";
            default -> "#607D8B";
        };
        popupRoot.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8; -fx-padding: 10;");
        popupRoot.setMaxWidth(300);
        popupRoot.setMinWidth(150);

        // Set up a text and style
        popupMessage.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-padding: 10");
        popupMessage.widthProperty().addListener((obs, oldVal, newVal) -> {
            popupRoot.setPrefWidth(Math.min(300, newVal.doubleValue() + 20));
        });

        popupRoot.setPrefHeight(75);


        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), popupRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), popupRoot);
        scaleUp.setFromX(0.8);
        scaleUp.setToX(1);
        scaleUp.setFromY(0.8);
        scaleUp.setToY(1);
        scaleUp.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), popupRoot);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.play();
        scaleUp.play();

        fadeIn.setOnFinished(e -> {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                fadeOut.play();
            }));
            timeline.setCycleCount(1);
            timeline.play();
        });
    }
}