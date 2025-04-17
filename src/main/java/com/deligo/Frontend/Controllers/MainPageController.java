package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
            //Centrovanie okna na stred main page
            Stage primaryStage = (Stage) goToCallService.getScene().getWindow();
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2;
            popupStage.setX(centerX - popupStage.getWidth() / 2);
            popupStage.setY(centerY - popupStage.getHeight() / 2);


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
            //Centrovanie okna na stred main page
            Stage primaryStage = (Stage) goToCallService.getScene().getWindow();
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2;
            popupStage.setX(centerX - popupStage.getWidth() / 2);
            popupStage.setY(centerY - popupStage.getHeight() / 2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/login_and_register_pop_up.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));

            // nastav vlastníka na hlavné okno (veľmi dôležité pre ďalší krok)
            Stage primaryStage = (Stage) goToCallService.getScene().getWindow();
            popupStage.initOwner(primaryStage);

            popupStage.show();

            // centrovanie
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2;
            popupStage.setX(centerX - popupStage.getWidth() / 2);
            popupStage.setY(centerY - popupStage.getHeight() / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//TEST SRANDA NA VELKOST OKNA
   /* public void handleInfoButtonClick() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/info_pop_up.fxml"));
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Zabezpečí, že pop-up je modal (blokuje hlavné okno)
        popupStage.initStyle(StageStyle.UNDECORATED);

        // Načítanie scény
        BorderPane root = loader.load();

        // Pridanie Border okraja
        root.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

        // Nastavenie scény s týmto rootom
        popupStage.setScene(new Scene(root));
        popupStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}*/

}
