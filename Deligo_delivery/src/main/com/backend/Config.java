package main.com.backend;

import main.com.backend.features.Backend;
import main.com.backend.features.FeatureValidateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);
    private static Backend backend;

    public static void initialize(Backend be) {
        backend = be;
    }

    public static Object routePost(String route, Object data) {
        logger.info("Routing POST request: " + route);
        switch (route) {
            case "/be/validateRequest":
                return backend.getFeatureValidateRequest().validateEmployeeRequest(data);
            default:
                return "Unknown POST route: " + route;
        }
    }

    public static Object routeGet(String route) {
        logger.info("Routing GET request: " + route);
        switch (route) {
            case "/be/status":
                return "Backend is running!";
            default:
                return "Unknown GET route: " + route;
        }
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
}