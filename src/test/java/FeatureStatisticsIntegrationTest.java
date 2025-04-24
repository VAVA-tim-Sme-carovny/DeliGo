import com.deligo.Backend.FeatureStatistics.FeatureStatistics;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.Assumptions;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureStatisticsIntegrationTest {

    private static LoggingAdapter logger;
    private static ConfigLoader configLoader;
    private static RestAPIServer restApiServer;
    private static FeatureStatistics featureStatistics;
    private static Gson gson = new Gson();
    private static boolean databaseAvailable = false;

    // Simple test implementation of LoggingAdapter
    static class TestLoggingAdapter extends LoggingAdapter {
        public TestLoggingAdapter() {
            super(null); // Pass null as LoggingWindow
        }
        
        @Override
        public void log(LogType type, LogPriority priority, LogSource source, String message) {
            // Just print to console for testing
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

        // 4. Create FeatureStatistics instance
        featureStatistics = new FeatureStatistics(configLoader, logger, restApiServer);
        assertNotNull(featureStatistics, "FeatureStatistics should be created");
        
        // 5. Check if database is available
        try {
            // Try to parse a valid JSON to check if the feature itself works
            // without actually connecting to the database
            String testJson = "{\"dummy\": \"test\"}";
            featureStatistics.getDailyStats(testJson);
            databaseAvailable = true;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            System.out.println("[TEST INFO] Database connection failed, tests will be skipped: " + e.getMessage());
            databaseAvailable = false;
        } catch (Exception e) {
            // If we get here, the database connection might be working but the test failed for other reasons
            databaseAvailable = true;
        }
    }

    @AfterAll
    static void tearDown() {
        // Clean up resources
    }

    @Test
    void testGetDailyStatsWithoutDate() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{}"; // Empty JSON object for today's date
        String responseJson = featureStatistics.getDailyStats(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        // Response could be 200 with data or 204 with no data
        int status = ((Double) response.get("status")).intValue();
        assertTrue(status == 200 || status == 204, "Expected status 200 or 204");
    }

    @Test
    void testGetDailyStatsWithInvalidDate() {
        String jsonData = "{\"date\": \"invalid-date\"}";
        String responseJson = featureStatistics.getDailyStats(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(500, response.getStatus(), "Expected status 500 for invalid date");
        assertTrue(response.getMessage().contains("Invalid date format"), 
                "Expected error message for invalid date format");
    }

    @Test
    void testGetDailyStatsWithValidDate() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"date\": \"2023-04-15\"}"; // Valid date in ISO format
        String responseJson = featureStatistics.getDailyStats(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        // Response could be 200 with data or 204 with no data
        int status = ((Double) response.get("status")).intValue();
        assertTrue(status == 200 || status == 204, "Expected status 200 or 204");
    }

    @Test
    void testGetStatsForRangeWithMissingParameters() {
        String jsonData = "{}"; // Missing fromDate and toDate
        String responseJson = featureStatistics.getStatsForRange(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for missing parameters");
        assertTrue(response.getMessage().contains("From date and To date are required"), 
                "Expected error message for missing date range");
    }

    @Test
    void testGetStatsForRangeWithInvalidDateFormat() {
        String jsonData = "{\"fromDate\": \"invalid-date\", \"toDate\": \"2023-04-15\"}";
        String responseJson = featureStatistics.getStatsForRange(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid date format");
        assertTrue(response.getMessage().contains("Invalid date format"), 
                "Expected error message for invalid date format");
    }

    @Test
    void testGetStatsForRangeWithInvalidDateRange() {
        String jsonData = "{\"fromDate\": \"2023-04-15\", \"toDate\": \"2023-04-01\"}"; // To date before from date
        String responseJson = featureStatistics.getStatsForRange(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid date range");
        assertTrue(response.getMessage().contains("From date must be before To date"), 
                "Expected error message for invalid date range");
    }

    @Test
    void testGetStatsForRangeWithValidDateRange() {
        Assumptions.assumeTrue(databaseAvailable, "Database is not available, skipping test");
        
        String jsonData = "{\"fromDate\": \"2023-04-01\", \"toDate\": \"2023-04-15\"}"; // Valid date range
        String responseJson = featureStatistics.getStatsForRange(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        // Response could be 200 with data or 204 with no data
        int status = ((Double) response.get("status")).intValue();
        assertTrue(status == 200 || status == 204, "Expected status 200 or 204");
    }

    @Test
    void testGetStatsWithInvalidJson() {
        String jsonData = "invalid-json";
        
        // Test getDailyStats with invalid JSON
        String responseJson1 = featureStatistics.getDailyStats(jsonData);
        Response response1 = gson.fromJson(responseJson1, Response.class);
        
        assertEquals(500, response1.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response1.getMessage().contains("Invalid request format"), 
                "Expected error message for invalid JSON format");
        
        // Test getStatsForRange with invalid JSON
        String responseJson2 = featureStatistics.getStatsForRange(jsonData);
        Response response2 = gson.fromJson(responseJson2, Response.class);
        
        assertEquals(500, response2.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response2.getMessage().contains("Invalid request format"), 
                "Expected error message for invalid JSON format");
    }
} 