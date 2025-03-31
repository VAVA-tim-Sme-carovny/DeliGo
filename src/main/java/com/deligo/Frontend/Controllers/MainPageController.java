package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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



    // Metóda na zmenu jazyka
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



    // Metóda na zavolanie obsluhy
    private void handleCallService() {
        System.out.println("Privolanie obsluhy...");
        // Tu môžeš pridať logiku na privolanie obsluhy (napr. zobraziť popup, poslať požiadavku, atď.)
    }


    // Metóda na zobrazenie pop up infopanel okna
    public void handleInfoButtonClick() {
        try {
            // Načítanie FXML súboru pre pop-up okno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/info_pop_up.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Zabezpečí, že pop-up je modal (blokuje hlavné okno)
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metóda na zobrazenie pop up okna pre rezerváciu stola
    public void bookTableClick() {
        try {
            // Načítanie FXML súboru pre pop-up okno
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/book_table_pop_up.fxml"));
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
