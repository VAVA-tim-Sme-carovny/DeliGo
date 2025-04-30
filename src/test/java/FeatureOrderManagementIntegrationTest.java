import com.deligo.Backend.FeatureOrderManagement.FeatureOrderManagement;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
// Samino skontroluj, ci toto funguje
public class FeatureOrderManagementIntegrationTest {

    private static LoggingAdapter logger;
    private static ConfigLoader configLoader;
    private static RestAPIServer restApiServer;
    private static FeatureOrderManagement featureOrderManagement;
    private static Gson gson = new Gson();
    private static boolean databaseAvailable = false;

    // Simple test implementation of LoggingAdapter
    static class TestLoggingAdapter extends LoggingAdapter {
        public TestLoggingAdapter() {
            super(null);
        }
        
        @Override
        public void log(LogType type, LogPriority priority, LogSource source, String message) {
            System.out.println(String.format("[TEST] %s [%s] [%s]: %s", type, priority, source, message));
        }
    }

    @BeforeAll
    static void setUp() throws IOException {
        // 1. Create a test logger
        logger = new TestLoggingAdapter();
        assertNotNull(logger, "Logger should be created");

        // 2. Load configuration
        configLoader = new ConfigLoader("src/main/resources/config.yaml");
        assertNotNull(configLoader, "ConfigLoader should be created");

        // 3. Start RestAPIServer
        restApiServer = new RestAPIServer(logger, configLoader);
        assertNotNull(restApiServer, "RestAPIServer should be created");

        // 4. Create FeatureOrderManagement instance
        featureOrderManagement = new FeatureOrderManagement(configLoader, logger, restApiServer);
        assertNotNull(featureOrderManagement, "FeatureOrderManagement should be created");
        
        // 5. Check if database is available
        try {
            String testJson = "{\"dummy\": \"test\"}";
            featureOrderManagement.getOrderById(testJson);
            databaseAvailable = true;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            System.out.println("[TEST INFO] Database connection failed, tests will be skipped: " + e.getMessage());
            databaseAvailable = false;
        } catch (Exception e) {
            databaseAvailable = true;
        }
    }

    @AfterAll
    static void tearDown() {
        // Clean up resources
    }

    @Test
    void testCreateOrderWithValidData() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{"
                + "\"tableId\": 1,"
                + "\"items\": ["
                + "{\"menuItemId\": 1, \"quantity\": 2},"
                + "{\"menuItemId\": 2, \"quantity\": 1}"
                + "]"
                + "}";
        
        String responseJson = featureOrderManagement.createOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(200, response.getStatus(), "Expected status 200 for valid order data");
        assertEquals("Order created successfully", response.getMessage());
    }

    @Test
    void testCreateOrderWithInvalidTableId() {
        String jsonData = "{"
                + "\"tableId\": -1," // Invalid table ID
                + "\"items\": ["
                + "{\"menuItemId\": 1, \"quantity\": 2}"
                + "]"
                + "}";
        
        String responseJson = featureOrderManagement.createOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid table ID");
        assertTrue(response.getMessage().contains("Invalid table ID"), 
                "Expected error message for invalid table ID");
    }

    @Test
    void testUpdateOrderStatus() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{"
                + "\"orderId\": 1,"
                + "\"status\": \"preparing\""
                + "}";
        
        String responseJson = featureOrderManagement.updateOrderStatus(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (response.getStatus() == 200) {
            assertEquals("Order status updated successfully", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("Order not found", response.getMessage());
        }
    }

    @Test
    void testGetOrderById() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"orderId\": 1}";
        String responseJson = featureOrderManagement.getOrderById(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        int status = ((Double)response.get("status")).intValue();
        assertTrue(status == 200 || status == 404, "Expected status 200 or 404");
    }

    @Test
    void testGetOrdersByTable() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"tableId\": 1}";
        String responseJson = featureOrderManagement.getOrdersByTable(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }

    @Test
    void testGetPendingOrders() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String responseJson = featureOrderManagement.getPendingOrders("{}");
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }

    @Test
    void testMarkOrderAsDelivered() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"orderId\": 1}";
        String responseJson = featureOrderManagement.markOrderAsDelivered(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (response.getStatus() == 200) {
            assertEquals("Order marked as delivered", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("Order not found", response.getMessage());
        }
    }

    @Test
    void testCancelOrder() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"orderId\": 1}";
        String responseJson = featureOrderManagement.cancelOrder(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (response.getStatus() == 200) {
            assertEquals("Order cancelled successfully", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("Order not found", response.getMessage());
        }
    }

    @Test
    void testWithInvalidJson() {
        String invalidJson = "invalid-json";
        
        Response[] responses = new Response[5];
        
        responses[0] = gson.fromJson(featureOrderManagement.createOrder(invalidJson), Response.class);
        responses[1] = gson.fromJson(featureOrderManagement.updateOrderStatus(invalidJson), Response.class);
        responses[2] = gson.fromJson(featureOrderManagement.getOrderById(invalidJson), Response.class);
        responses[3] = gson.fromJson(featureOrderManagement.getOrdersByTable(invalidJson), Response.class);
        responses[4] = gson.fromJson(featureOrderManagement.cancelOrder(invalidJson), Response.class);
        
        for (Response response : responses) {
            assertEquals(400, response.getStatus(), "Expected status 400 for invalid JSON");
            assertTrue(response.getMessage().contains("Invalid request format"), 
                    "Expected error message for invalid JSON format");
        }
    }
} 