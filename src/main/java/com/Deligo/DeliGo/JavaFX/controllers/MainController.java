package com.Deligo.DeliGo.JavaFX.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component // This ensures the controller is a Spring Bean
public class MainController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button myButton;

    @FXML
    private void handleButtonClick() {
        welcomeLabel.setText("Spring Boot + JavaFX is working!");
    }
}