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
            case "/updateLanguage":
                BaseFeature.updateLanguage(config);
            case "/update-info":
                return backend.getFeatureOrgDetails().updateOrgDetails(data);
            case "/":
                return backend.getFeatureCreateOrder().createOrder(data);

            // FeatureTableReservation paths
            case "/reservations/create":
                return backend.getFeatureTableReservation().createReservation(data);
            case "/reservations/get":
                return backend.getFeatureTableReservation().getReservationById(data);
            case "/reservations/user":
                return backend.getFeatureTableReservation().getReservationsByUser(data);
            case "/reservations/table":
                return backend.getFeatureTableReservation().getReservationsByTable(data);
            case "/reservations/cancel":
                return backend.getFeatureTableReservation().cancelReservation(data);

            // FeatureReview paths
            case "/reviews/add":
                return backend.getFeatureReview().addReview(data);
            case "/reviews/update":
                return backend.getFeatureReview().updateReview(data);
            case "/reviews/delete":
                return backend.getFeatureReview().removeReview(data);
            case "/reviews/get-by-user":
                return backend.getFeatureReview().getUserReviews(data);
            case "/reviews/get-by-menu-item":
                return backend.getFeatureReview().getReviewsByMenuItem(data);
            case "/reviews/get-by-id":
                return backend.getFeatureReview().getReviewById(data);
            case "/reviews/get-all":
                return backend.getFeatureReview().getAllReviews(data);

            // FeatureMenuManagement
            case "/menu/getAllItems":
                return backend.getFeatureMenuManagement().getAllItems("{}");
            case "/menu/getAllCategories":
                return backend.getFeatureMenuManagement().getAllCategories("{}");

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
