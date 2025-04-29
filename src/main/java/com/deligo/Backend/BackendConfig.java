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
            case "/org-details":
                return new Response(backend.getFeatureUserManagement().updateOrgDetails(data), 200);
            // FeatureStatistics
            case "/stats/daily":
                return new Response(backend.getFeatureStatistics().getDailyStats(data), 200);
            case "/stats/range":
                return new Response(backend.getFeatureStatistics().getStatsForRange(data), 200);

            // FeatureTableStructure
            case "/tables/getAll":
                return new Response(backend.getFeatureTableStructure().getAllTables("{}"), 200);

            // FeatureMenuManagement
            case "/menu/getAllItems":
                return new Response(backend.getFeatureMenuManagement().getAllItems("{}"), 200);
            case "/menu/getAllCategories":
                return new Response(backend.getFeatureMenuManagement().getAllCategories("{}"), 200);

            // FeatureUserManagement
            case "/users/getAll":
                return new Response(backend.getFeatureUserManagement().getAllUsers("{}"), 200);
            case "/get-all-users":
                return new Response(backend.getFeatureUserManagement().getAllUsers("{}"), 200);
            case "/orgDetails/get":
                return new Response(backend.getFeatureUserManagement().getOrgDetails("{}"), 200);
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
