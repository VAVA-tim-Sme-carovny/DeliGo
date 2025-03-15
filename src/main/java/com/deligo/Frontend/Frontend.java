package com.deligo.Frontend;

import com.deligo.Frontend.FeatureTestCommunication.FeatureTestCommunication;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.RestApi.RestAPIServer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Frontend {

    private FeatureTestCommunication featureTestCommunication;
    private RestAPIServer apiServer;
    private LoggingAdapter logger;

    private Stage primaryStage;
    private BorderPane rootLayout;

    public Frontend(RestAPIServer apiServer, LoggingAdapter logger) {
        this.apiServer = apiServer;
        this.logger = logger;

        // Inject FrontendConfig to REST API
        apiServer.setFrontendConfig(new FrontendConfig(this));

        // Inicializácia funkcií / features
        initializeFeatures();

        // Spustenie hlavného JavaFX okna
        launchFrontendWindow();
    }

    private void initializeFeatures() {
        featureTestCommunication = new FeatureTestCommunication(apiServer, logger);

        // Spusti test spojenia medzi FE a BE
        featureTestCommunication.testConnection();

        /* Pridávaj ďalšie featury tu */
    }

    private void launchFrontendWindow() {
        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, "Starting Frontend Main Window...");

        // JavaFX spúšťaj len v Platform.runLater
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                primaryStage.setTitle("DeliGo - Frontend");

                initRootLayout();

                primaryStage.show();

                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "Frontend Main Window launched successfully!");

            } catch (Exception e) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Failed to launch Frontend Main Window: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();

        // Príklad: Tu môžeš pridať komponenty, ktoré budú meniť obsah
        // rootLayout.setCenter(featureTestCommunication.getSomeComponent());

        Scene scene = new Scene(rootLayout, 900, 600);
        primaryStage.setScene(scene);
    }

    // Prístup k Feature objektom
    public FeatureTestCommunication getFeatureTestCommunication() {
        return featureTestCommunication;
    }

    // Tu budeš mať getter/setter pre manipuláciu s layoutom
    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
