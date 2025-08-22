import com.deligo.Backend.FeatureReview.FeatureReview;
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
public class FeatureReviewIntegrationTest {

    private static LoggingAdapter logger;
    private static ConfigLoader configLoader;
    private static RestAPIServer restApiServer;
    private static FeatureReview featureReview;
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

        // 4. Create FeatureReview instance
        featureReview = new FeatureReview(configLoader, logger, restApiServer);
        assertNotNull(featureReview, "FeatureReview should be created");
        
        // 5. Check if database is available
        try {
            String testJson = "{\"dummy\": \"test\"}";
            featureReview.getReviewById(testJson);
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
    void testAddReviewWithValidData() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{"
                + "\"userId\": 1,"
                + "\"menuItemId\": 1,"
                + "\"rating\": 5,"
                + "\"comment\": \"Excellent food!\""
                + "}";
        
        String responseJson = featureReview.addReview(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(200, response.getStatus(), "Expected status 200 for valid review data");
        assertEquals("Review added successfully", response.getMessage());
    }

    @Test
    void testAddReviewWithInvalidRating() {
        String jsonData = "{"
                + "\"userId\": 1,"
                + "\"menuItemId\": 1,"
                + "\"rating\": 6," // Invalid rating (should be 1-5)
                + "\"comment\": \"Excellent food!\""
                + "}";
        
        String responseJson = featureReview.addReview(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid rating");
        assertTrue(response.getMessage().contains("Rating must be between 1 and 5"), 
                "Expected error message for invalid rating");
    }

    @Test
    void testUpdateReview() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{"
                + "\"reviewId\": 1,"
                + "\"rating\": 4,"
                + "\"comment\": \"Updated review comment\""
                + "}";
        
        String responseJson = featureReview.updateReview(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (response.getStatus() == 200) {
            assertEquals("Review updated successfully", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("Review not found", response.getMessage());
        }
    }

    @Test
    void testRemoveReview() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"reviewId\": 1}";
        String responseJson = featureReview.removeReview(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        if (response.getStatus() == 200) {
            assertEquals("Review removed successfully", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("Review not found", response.getMessage());
        }
    }

    @Test
    void testGetUserReviews() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"userId\": 1}";
        String responseJson = featureReview.getUserReviews(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }

    @Test
    void testGetReviewsByMenuItem() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"menuItemId\": 1}";
        String responseJson = featureReview.getReviewsByMenuItem(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }

    @Test
    void testGetReviewById() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"reviewId\": 1}";
        String responseJson = featureReview.getReviewById(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        int status = ((Double)response.get("status")).intValue();
        assertTrue(status == 200 || status == 404, "Expected status 200 or 404");
    }

    @Test
    void testGetAllReviews() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String responseJson = featureReview.getAllReviews("{}");
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }

    @Test
    void testWithInvalidJson() {
        String invalidJson = "invalid-json";
        
        Response[] responses = new Response[5];
        
        responses[0] = gson.fromJson(featureReview.addReview(invalidJson), Response.class);
        responses[1] = gson.fromJson(featureReview.updateReview(invalidJson), Response.class);
        responses[2] = gson.fromJson(featureReview.removeReview(invalidJson), Response.class);
        responses[3] = gson.fromJson(featureReview.getUserReviews(invalidJson), Response.class);
        responses[4] = gson.fromJson(featureReview.getReviewsByMenuItem(invalidJson), Response.class);
        
        for (Response response : responses) {
            assertEquals(400, response.getStatus(), "Expected status 400 for invalid JSON");
            assertTrue(response.getMessage().contains("Invalid request format"), 
                    "Expected error message for invalid JSON format");
        }
    }
} 