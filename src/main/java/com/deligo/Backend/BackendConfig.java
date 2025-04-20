package com.deligo.Backend;

import com.deligo.Model.Response;

public class BackendConfig {

    private final Backend backend;

    public BackendConfig(Backend backend) {
        this.backend = backend;
    }

    public Response routePost(String path, String requestBody) {
        // FeatureValidateTestConnection paths
        if (path.equals("/testConnection")) {
            return new Response(backend.getFeatureValidateTestConnection().validateTestConnection(requestBody), 200);
        }

        // FeatureOrgDetails paths
        if (path.equals("/org-details")) {
            return new Response(backend.getFeatureUserManagement().updateOrgDetails(requestBody), 200);
        }

        // FeatureStatistics paths
        if (path.equals("/stats/daily")) {
            return new Response(backend.getFeatureStatistics().getDailyStats(requestBody), 200);
        }
        if (path.equals("/stats/range")) {
            return new Response(backend.getFeatureStatistics().getStatsForRange(requestBody), 200);
        }

        // FeatureTableStructure paths
        if (path.equals("/tables/add")) {
            return new Response(backend.getFeatureTableStructure().addTable(requestBody), 200);
        }
        if (path.equals("/tables/update")) {
            return new Response(backend.getFeatureTableStructure().updateTable(requestBody), 200);
        }
        if (path.equals("/tables/delete")) {
            return new Response(backend.getFeatureTableStructure().deleteTable(requestBody), 200);
        }
        if (path.equals("/tables/get-all")) {
            return new Response(backend.getFeatureTableStructure().getAllTables(requestBody), 200);
        }
        if (path.equals("/tables/get-by-category")) {
            return new Response(backend.getFeatureTableStructure().getTablesByCategory(requestBody), 200);
        }
        if (path.equals("/tables/categories/add")) {
            return new Response(backend.getFeatureTableStructure().addCategory(requestBody), 200);
        }
        if (path.equals("/tables/categories/update")) {
            return new Response(backend.getFeatureTableStructure().updateCategory(requestBody), 200);
        }
        if (path.equals("/tables/categories/delete")) {
            return new Response(backend.getFeatureTableStructure().deleteCategory(requestBody), 200);
        }
        if (path.equals("/tables/categories/get-all")) {
            return new Response(backend.getFeatureTableStructure().getAllCategories(requestBody), 200);
        }
        // FeatureMenuManagement paths
        if (path.equals("/menu/add-item")) {
            return new Response(backend.getFeatureMenuManagement().addItem(requestBody), 200);
        }
        if (path.equals("/menu/edit-item")) {
            return new Response(backend.getFeatureMenuManagement().updateItem(requestBody), 200);
        }
        if (path.equals("/menu/del-item")) {
            return new Response(backend.getFeatureMenuManagement().deleteItem(requestBody), 200);
        }
        if (path.equals("/menu/get-all-items")) {
            return new Response(backend.getFeatureMenuManagement().getAllItems(requestBody), 200);
        }
        if (path.equals("/menu/get-by-category")) {
            return new Response(backend.getFeatureMenuManagement().getItemsByCategory(requestBody), 200);
        }
        if (path.equals("/menu/add-category")) {
            return new Response(backend.getFeatureMenuManagement().addCategory(requestBody), 200);
        }
        if (path.equals("/menu/update-category")) {
            return new Response(backend.getFeatureMenuManagement().updateCategory(requestBody), 200);
        }
        if (path.equals("/menu/del-category")) {
            return new Response(backend.getFeatureMenuManagement().deleteCategory(requestBody), 200);
        }
        if (path.equals("/menu/get-all-categories")) {
            return new Response(backend.getFeatureMenuManagement().getAllCategories(requestBody), 200);
        }

        // FeatureUserManagement paths
        if (path.equals("/edit-user")) {
            return new Response(backend.getFeatureUserManagement().editUser(requestBody), 200);
        }
        if (path.equals("/del-user")) {
            return new Response(backend.getFeatureUserManagement().deleteUser(requestBody), 200);
        }
        if (path.equals("/get-all-users")) {
            return new Response(backend.getFeatureUserManagement().getAllUsers(requestBody), 200);
        }
        if (path.equals("/get-org-details")) {
            return new Response(backend.getFeatureUserManagement().getOrgDetails(requestBody), 200);
        }

        return new Response("Request not found for path: " + path, 404);
    }

    public Response routeGet(String path) {
        // Handle GET requests for all features
        
        // FeatureStatistics paths
        if (path.equals("/statistics/getAll")) {
            return new Response(backend.getFeatureStatistics().getDailyStats("{}"), 200);
        }
        
        // FeatureTableStructure paths
        if (path.equals("/tables/getAll")) {
            return new Response(backend.getFeatureTableStructure().getAllTables("{}"), 200);
        }
        
        // FeatureMenuManagement paths
        if (path.equals("/menu/getAllItems")) {
            return new Response(backend.getFeatureMenuManagement().getAllItems("{}"), 200);
        }
        if (path.equals("/menu/getAllCategories")) {
            return new Response(backend.getFeatureMenuManagement().getAllCategories("{}"), 200);
        }
        
        // FeatureUserManagement paths
        if (path.equals("/users/getAll")) {
            return new Response(backend.getFeatureUserManagement().getAllUsers("{}"), 200);
        }
        if (path.equals("/get-all-users")) {
            return new Response(backend.getFeatureUserManagement().getAllUsers("{}"), 200);
        }
        if (path.equals("/orgDetails/get")) {
            return new Response(backend.getFeatureUserManagement().getOrgDetails("{}"), 200);
        }
        
        // Add a debug response
        return new Response("GET request received for path: " + path, 200);
    }
}
