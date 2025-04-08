package com.deligo.Backend;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;


public class BackendConfig {
    private Backend backend;
    private ConfigLoader config;

    public BackendConfig(Backend be) {
        this.backend = be;
    }

    public Object routePost(String route, String data) {
        switch (route) {
            case "/testConnection":
                return backend.getFeatureValidateTestConnection().validateTestConnection(data);
            case "/api/be/updateLanguage":
                BaseFeature.updateLanguage(config);
            case "/api/be/register":
                return backend.getFeatureUserRegister().createAccount(data);
            default:
                return "Unknown POST route: " + route;
        }
    }

    public Object routeGet(String route) {
        return "Backend GET response (unused in test connection)";
    }
}
