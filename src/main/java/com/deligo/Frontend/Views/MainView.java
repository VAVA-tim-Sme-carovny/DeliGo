package com.deligo.Frontend.Views;

import com.deligo.Frontend.Controllers.FrontendController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX "View" pre hlavné okno frontendu.
 */
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
        rootLayout = new BorderPane();

        Scene scene = new Scene(rootLayout, 900, 600);
        primaryStage.setScene(scene);
    }
}
