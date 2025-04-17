package com.deligo.Frontend.Views;

import com.deligo.Frontend.Controllers.FrontendController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


public class MainView {

    private final FrontendController controller;
    private final LoggingAdapter logger;

    private Stage primaryStage;
    private BorderPane rootLayout;

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

    // Inicializácia hlavného okna
    private void initRootLayout() {
        rootLayout = new BorderPane();

        try {
            // Load custom top bar (navigation bar)
            /*FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("/Frontend_fxml/top_bar.fxml"));
            Parent topBar = topBarLoader.load();

            // Get the controller for top bar and set the MainView reference
            TopBarController topBarController = topBarLoader.getController();
            topBarController.setMainView(this);

            // Set the top bar in the BorderPane
            rootLayout.setTop(topBar);*/

            // Set initial content (page 1)
            loadPage("main_page");

            // Create and set the scene
            Scene scene = new Scene(rootLayout, 900, 600);
            primaryStage.setScene(scene);

            //Minimálna velkosť okna
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                    "Failed to load MainPage FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Funkcia na zmenu jazyka
    public void loadPage(String pageName) {
        try {
            // Získa aktuálne nastavený jazyk (napr. sk alebo en)
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());

            // Načíta FXML s lokalizačným bundle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/" + pageName.toLowerCase().replace(" ", "_") + ".fxml"), bundle);
            Parent page = loader.load();

            // Nastaví FXML do stredu rozloženia
            rootLayout.setCenter(page);

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                    "Failed to load " + pageName + " FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
