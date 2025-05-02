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
            case "/update-info":
                backend.getFeatureOrgDetails().updateOrgDetails(data);
            case "/":
                backend.getFeatureCreateOrder().createOrder(data);
            // FeatureTableReservation paths
            case "/reservations/create":
                return new Response(backend.getFeatureTableReservation().createReservation(data), 200);
            case "/reservations/get":
                return new Response(backend.getFeatureTableReservation().getReservationById(data), 200);
            case "/reservations/user":
                return new Response(backend.getFeatureTableReservation().getReservationsByUser(data), 200);
            case "/reservations/table":
                return new Response(backend.getFeatureTableReservation().getReservationsByTable(data), 200);
            case "/reservations/cancel":
                return new Response(backend.getFeatureTableReservation().cancelReservation(data), 200);
            // FeatureReview paths
            case "/reviews/add":
                return new Response(backend.getFeatureReview().addReview(data), 200);
            case "/reviews/update":
                return new Response(backend.getFeatureReview().updateReview(data), 200);
            case "/reviews/delete":
                return new Response(backend.getFeatureReview().removeReview(data), 200);
            case "/reviews/get-by-user":
                return new Response(backend.getFeatureReview().getUserReviews(data), 200);
            case "/reviews/get-by-menu-item":
                return new Response(backend.getFeatureReview().getReviewsByMenuItem(data), 200);
            case "/reviews/get-by-id":
                return new Response(backend.getFeatureReview().getReviewById(data), 200);
            case "/reviews/get-all":
                return new Response(backend.getFeatureReview().getAllReviews(data), 200);


            // FeatureStatistics
            case "/stats/daily":
                return new Response(backend.getFeatureStatistics().getDailyStats(data), 200);
            case "/stats/range":
                return new Response(backend.getFeatureStatistics().getStatsForRange(data), 200);

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
