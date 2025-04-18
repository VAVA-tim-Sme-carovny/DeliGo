package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class infoPopUpController {

    @FXML
    private Button closeButton;  // Priradíme tlačidlo "close" v FXML pomocou fx:id

    @FXML
    private void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow(); // Použijeme tlačidlo pre získanie scény
        stage.close();
    }
}
