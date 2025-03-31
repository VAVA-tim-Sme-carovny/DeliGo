package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class loginAndRegisterPopUpController {

    @FXML
    private Button closeButton;  // Priradíme tlačidlo "close" v FXML pomocou fx:id

    @FXML
    private Button closeButton2;

    @FXML
    private void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow(); // Použijeme tlačidlo pre získanie scény
        stage.close();
    }

    @FXML
    private void closePopup2() {
        Stage stage = (Stage) closeButton2.getScene().getWindow(); // Použijeme tlačidlo pre získanie scény
        stage.close();
    }


    public void openRegisterWindowPopup() {
        try {
            // Načítanie FXML súboru pre pop-up okno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/confirm_registration_pop_up.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Zabezpečí, že pop-up je modal (blokuje hlavné okno)
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

