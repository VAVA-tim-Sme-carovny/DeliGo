package com.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);
    private static Backend backend;

    public static void initialize(Backend be) {
        backend = be;
    }

    public static Object routeGet(String route) {
        logger.info("🔍 Routing GET request: " + route);
        switch (route) {
            case "/be/validateRequest":
                return (backend != null) ? backend.getFeatureValidateRequest().validateEmployeeRequest(null) : "Backend not initialized!";
            default:
                return "❌ Unknown GET route: " + route;
        }
    }

    public static Object routePost(String route, Object data) {
        logger.info("🔍 Routing POST request: " + route);
        switch (route) {
            case "/be/validateRequest":
                return (backend != null) ? backend.getFeatureValidateRequest().validateEmployeeRequest(data) : "Backend not initialized!";
            default:
                return "❌ Unknown POST route: " + route;
        }
    }
}