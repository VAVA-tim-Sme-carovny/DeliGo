//package com;
//
//import com.api.server.SimpleHttpServer;
//import com.backend.Backend;
//import com.backend.Config;
//import javafx.application.Application;
//import javafx.stage.Stage;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.IOException;
//
//public class MainApp extends Application {
//    private static final Logger logger = LogManager.getLogger(MainApp.class);
//    private static final boolean RUN_BACKEND = Boolean.parseBoolean(System.getProperty("runBackend", "true"));
//    private static final boolean RUN_FRONTEND = Boolean.parseBoolean(System.getProperty("runFrontend", "true"));
//
//    public static void main(String[] args) {
//        try {
//            startRestAPI();  // Always start REST API first
//            if (RUN_BACKEND) {
//                startBackend();
//            }
//            if (RUN_FRONTEND) {
//                launch(args); // Starts JavaFX UI
//            }
//        } catch (Exception e) {
//            logger.error("❌ Application startup failed!", e);
//        } finally {
//            if (RUN_BACKEND) {
//                stopBackend();
//            }
//        }
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Tracking App - FE");
//        primaryStage.show();
//        logger.info("✅ Frontend (JavaFX) started.");
//    }
//
//    private static void startRestAPI() {
//        try {
//            logger.info("🚀 Starting REST API server...");
//            SimpleHttpServer.startServer();
//        } catch (IOException e) {
//            logger.error("❌ Failed to start REST API server", e);
//        }
//    }
//
//    private static void startBackend() {
//        logger.info("🚀 Starting Backend Services...");
//        Backend backend = new Backend();
//        Config.initialize(backend); // Set up the backend in Config
//    }
//
//    private static void stopBackend() {
//        logger.info("🛑 Stopping backend services...");
//    }
//}

package com;

import com.api.server.SimpleHttpServer;
import com.FrontendLauncher;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp {
    private static final Logger logger = LogManager.getLogger(MainApp.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("❌ No argument provided. Use 'restapi', 'backend', or 'frontend'");
            System.exit(1);
        }

        String mode = args[0].toLowerCase();

        try {
            switch (mode) {
                case "restapi":
                    startRestAPI();
                    break;
                case "backend":
                    startBackend();
                    break;
                case "frontend":
                    FrontendLauncher.main(args);
                    break;
                default:
                    logger.error("❌ Invalid argument '{}'. Use 'restapi', 'backend', or 'frontend'", mode);
                    System.exit(1);
            }
        } catch (Exception e) {
            logger.error("❌ Error during startup: ", e);
            System.exit(1);
        }
    }

    private static void startRestAPI() {
        try {
            logger.info("🚀 Starting REST API server...");
            SimpleHttpServer.startServer();
        } catch (Exception e) {
            logger.error("❌ Failed to start REST API", e);
        }
    }

    private static void startBackend() {
        logger.info("🚀 Starting Backend Services...");
        // Add backend-specific startup logic here
    }
}