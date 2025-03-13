package main.com;

import main.com.api.server.SimpleHttpServer;
import main.com.logging.LoggerConfig;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MainApp extends Application {
    private static final Logger logger = LogManager.getLogger(MainApp.class);
    private static final boolean RUN_BACKEND = Boolean.parseBoolean(System.getProperty("runBackend", "true"));
    private static final boolean RUN_FRONTEND = Boolean.parseBoolean(System.getProperty("runFrontend", "true"));

    public static void main(String[] args) {
        try {
            if (RUN_BACKEND) {
                startBackend();
            }
            if (RUN_FRONTEND) {
                launch(args); // Starts JavaFX UI
            }
        } catch (Exception e) {
            logger.error("Application startup failed!", e);
        } finally {
            if (RUN_BACKEND) {
                stopBackend();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tracking App - FE");
        primaryStage.show();
        logger.info("Frontend (JavaFX) started.");
    }

    private static void startBackend() {
        try {
            logger.info("Starting REST API server...");
            SimpleHttpServer.startServer();
        } catch (IOException e) {
            logger.error("Failed to start REST API server", e);
        }
    }

    private static void stopBackend() {
        logger.info("Stopping backend services...");
        SimpleHttpServer.stopServer();
    }
}