package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private Button closeButton3;


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

    @FXML
    private void closePopup3() {
        Stage stage = (Stage) closeButton3.getScene().getWindow(); // Použijeme tlačidlo pre získanie scény
        stage.close();
    }


    public void openRegisterWindowPopup() {
        try {
            // Načítanie FXML súboru pre pop-up okno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/confirm_registration_pop_up.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Zabezpečí, že pop-up je modal (blokuje hlavné okno)
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openOrderMenu() {
        try {
            // Získaj stage z prihlasovacieho okna
            Stage loginStage = (Stage) closeButton.getScene().getWindow();

            // Získaj referenciu na hlavné okno (owner prihlasovacieho popupu)
            Stage mainStage = (Stage) loginStage.getOwner();

            // Načítaj FXML pre objednávacie menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/order_menu.fxml"));
            Parent root = loader.load();

            // Vytvor nové okno
            Stage orderMenuStage = new Stage();
            orderMenuStage.initModality(Modality.APPLICATION_MODAL);
            orderMenuStage.initStyle(StageStyle.UNDECORATED);
            orderMenuStage.initOwner(mainStage); // nastav vlastníka

            // Nastav veľkosť podľa hlavného okna
            orderMenuStage.setWidth(mainStage.getWidth());
            orderMenuStage.setHeight(mainStage.getHeight());

            // Nastav pozíciu podľa hlavného okna
            orderMenuStage.setX(mainStage.getX());
            orderMenuStage.setY(mainStage.getY());

            // Zobraz nové popup okno
            orderMenuStage.setScene(new Scene(root));
            orderMenuStage.show();

            // Zatvor prihlasovacie okno
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}

