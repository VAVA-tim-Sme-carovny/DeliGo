package com.deligo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FrontendLauncher extends Application {
    private static final Logger logger = LogManager.getLogger(FrontendLauncher.class);

    public static void main(String[] args) {
        logger.info("🟢 Launching JavaFX Frontend...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tracking App - FE");
        logger.info("✅ JavaFX UI initialized successfully.");

        primaryStage.setOnCloseRequest(event -> {
            logger.info("🛑 Closing Frontend Application.");
        });

        primaryStage.show();
    }
}