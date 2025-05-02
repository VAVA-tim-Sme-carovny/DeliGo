package com.deligo.Frontend.Views;

import com.deligo.Frontend.Controllers.FrontendController;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.Views;
import com.deligo.Utils.UTF8Control;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


public class MainView {

    private final FrontendController controller;
    private final LoggingAdapter logger;

    private Stage primaryStage;
    private GridPane rootLayout;

    public MainView(FrontendController controller, LoggingAdapter logger) {
        this.controller = controller;
        this.logger = logger;
    }

    public void launchWindow() {

        // Nastavíme predvolený jazyk na slovenčinu
        Locale.setDefault(new Locale("sk", "SK"));

        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, "Starting Frontend Main Window...");

        // Spúšťame JavaFX kód
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                primaryStage.setTitle("DeliGo - Frontend");

                initRootLayout();

                primaryStage.show();
                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND,
                        "Frontend Main Window launched successfully!");

            } catch (Exception e) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                        "Failed to launch Frontend Main Window: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void initRootLayout() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault(), new UTF8Control());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/main_view.fxml"), bundle);

            // ✅ Vytvoríme vlastný controller (lebo nemáme fx:controller vo FXML)
            MainPageController controller = new MainPageController(
                    this.controller.getConfig(),
                    this.logger,
                    this.controller.getApiServer()
            );
            loader.setController(controller); // Musí byť pred loader.load()

            Parent root = loader.load();
            rootLayout = (GridPane) root;

            // ✅ Môžeme volať metódy na controlleri
            controller.clearAll();
            controller.loadView("/Views/Controllers/EmployeeTopPanel.fxml", Views.controllerPanel);
            controller.loadView("/Views/Controllers/Employee/AdminMenuController.fxml", Views.leftPanel);
            //controller.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);

            // Nastav fixnú veľkosť okna
            Scene scene = new Scene(rootLayout, 1000, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("DeliGo - Frontend");
            primaryStage.setResizable(true); // Zakáže zmenu veľkosti okna
            primaryStage.centerOnScreen();    // Vycentruje okno na obrazovku
            primaryStage.show();

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                    "Failed to load Main View: " + e.getMessage());
            e.printStackTrace();
        }
    }



//    // Funkcia na zmenu jazyka
    @FXML
    private void switchToEnglish() {
        System.out.println("Klik na EN");
        switchLanguage("en");
    }

    @FXML
    private void switchToSlovak() {
        switchLanguage("sk");
    }



    public void switchLanguage(String langCode) {
        try {
            Locale.setDefault(new Locale(langCode));
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault(), new UTF8Control());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/main_view.fxml"), bundle);
            Parent page = loader.load();

//             Nastaví FXML do stredu rozloženia

//            rootLayout.add(page, 1, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
