package com.deligo.Backend.FeatureOrderProcessing;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureOrderProcessingIntegrationTest extends com.deligo.Backend.BaseFeature.BaseIntegrationTest {
    private static final Gson gson = new Gson();
    private FeatureOrderProcessing featureOrderProcessing;

    @BeforeEach
    void setUpFeature() {
        // Create a new instance of FeatureOrderProcessing with initialized dependencies
        featureOrderProcessing = new FeatureOrderProcessing(configLoader, logger, restApiServer);
    }

    @AfterAll
    static void tearDown() {
        // Clean up resources if needed
    }

    @Test
    void notifyNewOrderValidInput() {
        // Test with a valid order ID
        String responseJson = featureOrderProcessing.notifyNewOrder(1);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("New order waiting") || 
                   response.getMessage().contains("Nová objednávka"),
                "Expected success message for notification");
    }

    @Test
    void notifyNewOrderInvalidInput() {
        // Test with an invalid order ID (assuming order ID 999 doesn't exist)
        String responseJson = featureOrderProcessing.notifyNewOrder(999);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid order ID");
        assertTrue(response.getMessage().contains("not found") || 
                   response.getMessage().contains("nebola nájdená"),
                "Expected error message for order not found");
    }

    @Test
    void confirmOrderValidInput() {
        String jsonData = "{\n" +
                "  \"orderId\": 1\n" +
                "}";
        String responseJson = featureOrderProcessing.confirmOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("confirmed") || 
                   response.getMessage().contains("potvrdená"),
                "Expected success message for order confirmation");
    }

    @Test
    void confirmOrderInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureOrderProcessing.confirmOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Invalid JSON") || 
                   response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void confirmOrderInvalidOrderId() {
        String jsonData = "{\n" +
                "  \"orderId\": 999\n" +
                "}";
        String responseJson = featureOrderProcessing.confirmOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid order ID");
        assertTrue(response.getMessage().contains("not found") || 
                   response.getMessage().contains("nebola nájdená"),
                "Expected error message for order not found");
    }

    @Test
    void rejectOrderValidInput() {
        String jsonData = "{\n" +
                "  \"orderId\": 1,\n" +
                "  \"reason\": \"Out of ingredients\"\n" +
                "}";
        String responseJson = featureOrderProcessing.rejectOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("rejected") || 
                   response.getMessage().contains("zamietnutá"),
                "Expected success message for order rejection");
    }

    @Test
    void rejectOrderInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureOrderProcessing.rejectOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Invalid JSON") || 
                   response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void rejectOrderInvalidOrderId() {
        String jsonData = "{\n" +
                "  \"orderId\": 999,\n" +
                "  \"reason\": \"Out of ingredients\"\n" +
                "}";
        String responseJson = featureOrderProcessing.rejectOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid order ID");
        assertTrue(response.getMessage().contains("not found") || 
                   response.getMessage().contains("nebola nájdená"),
                "Expected error message for order not found");
    }

    @Test
    void markItemAsPreparedValidInput() {
        String jsonData = "{\n" +
                "  \"itemId\": 1\n" +
                "}";
        String responseJson = featureOrderProcessing.markItemAsPrepared(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("prepared") || 
                   response.getMessage().contains("pripravená"),
                "Expected success message for item prepared");
    }

    @Test
    void markItemAsPreparedInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureOrderProcessing.markItemAsPrepared(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Invalid JSON") || 
                   response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void markItemAsPreparedInvalidItemId() {
        String jsonData = "{\n" +
                "  \"itemId\": 999\n" +
                "}";
        String responseJson = featureOrderProcessing.markItemAsPrepared(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid item ID");
        assertTrue(response.getMessage().contains("not found") || 
                   response.getMessage().contains("nebola nájdená"),
                "Expected error message for item not found");
    }

    @Test
    void markItemAsDeliveredValidInput() {
        String jsonData = "{\n" +
                "  \"itemId\": 1\n" +
                "}";
        String responseJson = featureOrderProcessing.markItemAsDelivered(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("delivered") || 
                   response.getMessage().contains("doručená"),
                "Expected success message for item delivered");
    }

    @Test
    void markItemAsDeliveredInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureOrderProcessing.markItemAsDelivered(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Invalid JSON") || 
                   response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void markItemAsDeliveredInvalidItemId() {
        String jsonData = "{\n" +
                "  \"itemId\": 999\n" +
                "}";
        String responseJson = featureOrderProcessing.markItemAsDelivered(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid item ID");
        assertTrue(response.getMessage().contains("not found") || 
                   response.getMessage().contains("nebola nájdená"),
                "Expected error message for item not found");
    }

    @Test
    void getReadyItems() {
        String responseJson = featureOrderProcessing.getReadyItems();
        
        // Parse the response as a list of OrderItem objects
        Type listType = new TypeToken<List<OrderItem>>(){}.getType();
        List<OrderItem> readyItems = gson.fromJson(responseJson, listType);
        
        // Verify that the response is a valid JSON array
        assertNotNull(readyItems, "Expected a valid list of ready items");
        
        // Verify that all items have the READY status
        for (OrderItem item : readyItems) {
            assertEquals(BasicModels.OrderState.READY.getValue(), item.getStatus(), 
                    "Expected all items to have READY status");
        }
    }
}