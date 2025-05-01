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


                // Nove end pointy pre DB

            // FeatureOrgDetails - Nove
            case "/org-get":
                return new Response(backend.getFeatureOrgDetails().getOrgDetails(data), 200);
            case "/org-update":
                return new Response(backend.getFeatureUserManagement().updateOrgDetails(data), 200);
            case "/org-open":
                return new Response(backend.getFeatureOrgDetails().getOpeningHours(data), 200);
            // FeatureOrderManagement paths
            case "/orders/create":
                return new Response(backend.getFeatureOrderManagement().createOrder(data), 200);
            case "/orders/update":
                return new Response(backend.getFeatureOrderManagement().updateOrder(data), 200);
            case "/orders/update-status":
                return new Response(backend.getFeatureOrderManagement().updateOrderStatus(data), 200);
            case "/orders/get":
                return new Response(backend.getFeatureOrderManagement().getOrderById(data), 200);
            case "/orders/table":
                return new Response(backend.getFeatureOrderManagement().getOrdersByTable(data), 200);
            case "/orders/pending":
                return new Response(backend.getFeatureOrderManagement().getPendingOrders(data), 200);
            case "/orders/delivered":
                return new Response(backend.getFeatureOrderManagement().markOrderAsDelivered(data), 200);
            case "/orders/cancel":
                return new Response(backend.getFeatureOrderManagement().cancelOrder(data), 200);


            // Pravdepodobne prec
            case "/menu/category":
                return new Response(backend.getFeatureOrderManagement().getMenuByCategory(data), 200);
            case "/menu/categories":
                return new Response(backend.getFeatureOrderManagement().getCategories(data), 200);


            // FeatureTableReservation paths
            case "/reservations/create":
                return new Response(backend.getFeatureTableReservation().createReservation(data), 200);
            case "/reservations/update-status":
                return new Response(backend.getFeatureTableReservation().updateReservationStatus(data), 200);
            case "/reservations/get":
                return new Response(backend.getFeatureTableReservation().getReservationById(data), 200);
            case "/reservations/user":
                return new Response(backend.getFeatureTableReservation().getReservationsByUser(data), 200);
            case "/reservations/table":
                return new Response(backend.getFeatureTableReservation().getReservationsByTable(data), 200);
            case "/reservations/cancel":
                return new Response(backend.getFeatureTableReservation().cancelReservation(data), 200);
            case "/reservations/available-tables":
                return new Response(backend.getFeatureTableReservation().getAvailableTables(data), 200);
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
            // FeatureTableStructure
            case "/tables/getAll":
                return new Response(backend.getFeatureTableStructure().getAllTables(data), 200);


            // FeatureTableStructure paths
            case "/tables/add":
                return new Response(backend.getFeatureTableStructure().addTable(data), 200);
            case "/tables/update":
                return new Response(backend.getFeatureTableStructure().updateTable(data), 200);
            case "/tables/delete":
                return new Response(backend.getFeatureTableStructure().deleteTable(data), 200);
            case "/tables/get-all":
                return new Response(backend.getFeatureTableStructure().getAllTables(data), 200);


            // Pravdepodobne vymazat
            case "/tables/get-by-category":
                return new Response(backend.getFeatureTableStructure().getTablesByCategory(data), 200);
            case "/tables/categories/add":
                return new Response(backend.getFeatureTableStructure().addCategory(data), 200);
            case "/tables/categories/update":
                return new Response(backend.getFeatureTableStructure().updateCategory(data), 200);
            case "/tables/categories/delete":
                return new Response(backend.getFeatureTableStructure().deleteCategory(data), 200);
            case "/tables/categories/get-all":
                return new Response(backend.getFeatureTableStructure().getAllCategories(data), 200);


            // FeatureMenuManagement paths
            case "/menu/addItem":
                return new Response(backend.getFeatureMenuManagement().addItem(data), 200);
            case "/menu/editItem":
                return new Response(backend.getFeatureMenuManagement().updateItem(data), 200);
            case "/menu/delItem":
                return new Response(backend.getFeatureMenuManagement().deleteItem(data), 200);
            case "/menu/getByCategory":
                return new Response(backend.getFeatureMenuManagement().getItemsByCategory(data), 200);
            case "/menu/addCategory":
                return new Response(backend.getFeatureMenuManagement().addCategory(data), 200);
            case "/menu/updateCategory":
                return new Response(backend.getFeatureMenuManagement().updateCategory(data), 200);
            case "/menu/delCategory":
                return new Response(backend.getFeatureMenuManagement().deleteCategory(data), 200);
            case "/menu/getAllCategories":
                return new Response(backend.getFeatureMenuManagement().getAllCategories(data), 200);
            case "/menu/getAllItems":
                return new Response(backend.getFeatureMenuManagement().getAllItems("{}"), 200);


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
