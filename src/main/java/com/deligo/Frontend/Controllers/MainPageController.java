package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainPageController {

    @FXML
    private void switchToEnglish() {
        System.out.println("Klik na EN");
        switchLanguage("en");
    }

    @FXML
    private void switchToSlovak() {
        switchLanguage("sk");
    }

    @FXML
    private Button btnSk;

    @FXML
    private Button btnEn;


    private void switchLanguage(String langCode) {
        try {
            // Nastaví nový jazyk
            Locale.setDefault(new Locale(langCode));
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());

            // Načíta nové FXML s lokalizáciou
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/main_page.fxml"), bundle);
            Parent root = loader.load();

            // Získa stage zo starého prvku, ktorý je na obrazovke
            Stage stage = (Stage) goToCallService.getScene().getWindow();

            // Vymení obsah scény za nový root s novou lokalizáciou
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Button goToCallService;

    @FXML
    public void initialize() {
        System.out.println("MainPageNewController initialized.");

        // Pridaj listener na kliknutie
        goToCallService.setOnAction(event -> handleCallService());
    }

    private void handleCallService() {
        System.out.println("Privolanie obsluhy...");
        // Tu môžeš pridať logiku na privolanie obsluhy (napr. zobraziť popup, poslať požiadavku, atď.)
    }
}
