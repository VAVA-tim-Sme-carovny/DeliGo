package com.deligo.Frontend;

public class FrontendConfig {
    private Frontend frontend;

    public FrontendConfig(Frontend fe) {
        this.frontend = fe;
    }

    public Object routePost(String route, String data) {
        switch (route) {
            case "/api/fe/sendForm":
                frontend.getController().getFeatureTestCommunication().testConnection();
            default:
                return "Unknown POST route: " + route;
        }
    }

    public Object routeGet(String route) {
        switch (route) {
            case "/api/fe/health":
                return "Frontend is healthy!";
            default:
                return "Unknown GET route: " + route;
        }
    }
}
