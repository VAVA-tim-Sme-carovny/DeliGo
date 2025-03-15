package com.deligo.Backend;

public class BackendConfig {
    private Backend backend;

    public BackendConfig(Backend be) {
        this.backend = be;
    }

    public Object routePost(String route, String data) {
        switch (route) {
            case "/api/be/testConnection":
                return backend.getFeatureValidateTestConnection().validateTestConnection(data);
            default:
                return "Unknown POST route: " + route;
        }
    }

    public Object routeGet(String route) {
        return "Backend GET response (unused in test connection)";
    }
}
