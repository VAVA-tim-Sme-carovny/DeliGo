package com.deligo.Backend;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Model.Response;


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
            case "/updateLanguage":
                BaseFeature.updateLanguage(config);
            // FeatureOrgDetails
            case "/update-info":
                backend.getFeatureOrgDetails().updateOrgDetails(data);
            // FeatureStatistics
            case "/stats/daily":
                return new Response(backend.getFeatureStatistics().getDailyStats(data), 200);
            case "/stats/range":
                return new Response(backend.getFeatureStatistics().getStatsForRange(data), 200);

            // FeatureTableStructure
            case "/devices/update":
                return backend.getFeatureTableStructure().editTable(data);
            case "/devices/delete":
                return backend.getFeatureTableStructure().deleteTable(data);

            // FeatureMenuManagement
            case "/menu/getAllItems":
                return new Response(backend.getFeatureMenuManagement().getAllItems("{}"), 200);
            case "/menu/getAllCategories":
                return new Response(backend.getFeatureMenuManagement().getAllCategories("{}"), 200);

            // FeatureUserManagement
            case "/edit-user":
                return backend.getFeatureUserManagement().editUser(data);
            case "/register":
                return backend.getFeatureUserRegister().createAccount(data);
            case "/login/customer":
                return backend.getFeatureUserLogin().loginCustomer();
            case "/login/employee":
                return backend.getFeatureUserLogin().loginEmployee(data);
            case "/logout":
                return backend.getFeatureUserLogin().logout();
            default:
                return "Unknown POST route: " + route;
        }
    }

    public Object routeGet(String route) {
        return "Backend GET response (unused in test connection)";
    }
}
