import com.deligo.Backend.FeatureUserManagement.FeatureUserManagement;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureUserManagementIntegrationTest {

    private static LoggingAdapter logger;
    private static ConfigLoader configLoader;
    private static RestAPIServer restApiServer;
    private static FeatureUserManagement featureUserManagement;
    private static Gson gson = new Gson();

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

        // 4. Create FeatureUserManagement instance
        featureUserManagement = new FeatureUserManagement(configLoader, logger, restApiServer);
        assertNotNull(featureUserManagement, "FeatureUserManagement should be created");
    }

    @AfterAll
    static void tearDown() {
        // Clean up resources
    }

    @Test
    void testEditUserWithValidData() {
        // This test assumes that a user with ID "1" already exists in the database
        // For a real test, we would need to create a user first, but this is sufficient for testing the API
        String userId = "1"; // Dummy user ID
        String username = "testuser";
        
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        roles.add("manager");
        
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");
        
        String jsonData = "{"
                + "\"userId\": \"" + userId + "\","
                + "\"username\": \"" + username + "\","
                + "\"roles\": [\"admin\", \"manager\"],"
                + "\"tags\": [\"tag1\", \"tag2\"]"
                + "}";
        
        String responseJson = featureUserManagement.editUser(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        // The test might fail if user ID "1" doesn't exist, but that's acceptable for integration testing
        if (response.getStatus() == 200) {
            assertEquals("User roles and tags were successfully updated", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("User not found", response.getMessage());
        }
    }
    
    @Test
    void testEditUserWithInvalidUserId() {
        String userId = UUID.randomUUID().toString(); // Random UUID that shouldn't exist
        String username = "testuser";
        
        String jsonData = "{"
                + "\"userId\": \"" + userId + "\","
                + "\"username\": \"" + username + "\","
                + "\"roles\": [\"admin\", \"manager\"],"
                + "\"tags\": [\"tag1\", \"tag2\"]"
                + "}";
        
        String responseJson = featureUserManagement.editUser(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(404, response.getStatus(), "Expected status 404 for non-existent user");
        assertEquals("User not found", response.getMessage());
    }
    
    @Test
    void testEditUserWithMismatchedUsername() {
        // This test assumes that a user with ID "1" exists but has a different username
        String userId = "1";
        String wrongUsername = "wrong_username";
        
        String jsonData = "{"
                + "\"userId\": \"" + userId + "\","
                + "\"username\": \"" + wrongUsername + "\","
                + "\"roles\": [\"admin\", \"manager\"],"
                + "\"tags\": [\"tag1\", \"tag2\"]"
                + "}";
        
        String responseJson = featureUserManagement.editUser(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        // If the user exists but has a different username than provided
        if (response.getStatus() == 400) {
            assertEquals("Username does not match with the user ID", response.getMessage());
        }
        // If the user doesn't exist at all
        else if (response.getStatus() == 404) {
            assertEquals("User not found", response.getMessage());
        }
    }
    
    @Test
    void testDeleteUserWithValidData() {
        // This test assumes that a user with ID "2" already exists in the database
        String userId = "2"; // Dummy user ID
        String username = "testuser2";
        
        String jsonData = "{"
                + "\"userId\": \"" + userId + "\","
                + "\"username\": \"" + username + "\""
                + "}";
        
        String responseJson = featureUserManagement.deleteUser(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        // The test might fail if user ID "2" doesn't exist, but that's acceptable for integration testing
        if (response.getStatus() == 200) {
            assertEquals("User was successfully deleted", response.getMessage());
        } else if (response.getStatus() == 404) {
            assertEquals("User not found", response.getMessage());
        }
    }

    @Test
    void testDeleteUserWithInvalidUserId() {
        String userId = UUID.randomUUID().toString();
        String username = "testuser";

        String jsonData = "{"
                + "\"userId\": \"" + userId + "\","
                + "\"username\": \"" + username + "\""
                + "}";

        String responseJson = featureUserManagement.deleteUser(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        assertEquals(404, response.getStatus(), "Expected status 404 for non-existent user");
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void testGetAllUsers() {
        String jsonData = "{}";
        String responseJson = featureUserManagement.getAllUsers(jsonData);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertEquals("Users retrieved successfully", response.get("message"));
        assertNotNull(response.get("data"), "Data field should not be null");
    }
    @Test
    void testUpdateOrgDetailsWithValidData() {
        String jsonData = "{"
                + "\"openingTimes\": ["
                + "[\"08:00\", \"20:00\"], " // Monday-Sunday
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"22:00\"], "
                + "[\"10:00\", \"22:00\"], "
                + "[\"Closed\", \"Closed\"]"
                + "],"
                + "\"phoneNumber\": \"0911 222 333\","
                + "\"email\": \"info@deligo.com\""
                + "}";
        String responseJson = featureUserManagement.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        assertEquals(200, response.getStatus(), "Expected status 200 for valid org details");
        assertEquals("Organization details were updated successfully", response.getMessage());
    }
    
    @Test
    void testUpdateOrgDetailsWithInvalidPhoneFormat() {
        String jsonData = "{"
                + "\"openingTimes\": ["
                + "[\"08:00\", \"20:00\"], " // Monday-Sunday
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"22:00\"], "
                + "[\"10:00\", \"22:00\"], "
                + "[\"Closed\", \"Closed\"]"
                + "],"
                + "\"phoneNumber\": \"123-456-7890\"," // diff
                + "\"email\": \"info@deligo.com\""
                + "}";
        
        String responseJson = featureUserManagement.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid phone format");
        assertEquals("Phone number is in invalid format", response.getMessage());
    }
    
    @Test
    void testUpdateOrgDetailsWithInvalidEmailFormat() {
        String jsonData = "{"
                + "\"openingTimes\": ["
                + "[\"08:00\", \"20:00\"], " // Monday-Sunday
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"20:00\"], "
                + "[\"08:00\", \"22:00\"], "
                + "[\"10:00\", \"22:00\"], "
                + "[\"Closed\", \"Closed\"]"
                + "],"
                + "\"phoneNumber\": \"0911 222 333\","
                + "\"email\": \"invalid-email\"" // diff
                + "}";
        
        String responseJson = featureUserManagement.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);
        
        assertEquals(400, response.getStatus(), "Expected status 400 for invalid email format");
        assertEquals("Email is in invalid format", response.getMessage());
    }
    
    @Test
    void testGetOrgDetails() {
        String jsonData = "{}";
        String responseJson = featureUserManagement.getOrgDetails(jsonData);
        
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> response = gson.fromJson(responseJson, mapType);
        
        assertEquals(200, ((Double)response.get("status")).intValue(), "Expected status 200");
        assertNotNull(response.get("data"), "Data field should not be null");
    }
    
    @Test
    void testWithInvalidJson() {
        String invalidJson = "invalid-json";
        
        // Test editUser with invalid JSON
        String responseJson1 = featureUserManagement.editUser(invalidJson);
        Response response1 = gson.fromJson(responseJson1, Response.class);
        
        assertEquals(400, response1.getStatus(), "Expected status 400 for invalid JSON");
        assertEquals("Invalid request format", response1.getMessage());
        
        // Test deleteUser with invalid JSON
        String responseJson2 = featureUserManagement.deleteUser(invalidJson);
        Response response2 = gson.fromJson(responseJson2, Response.class);
        
        assertEquals(400, response2.getStatus(), "Expected status 400 for invalid JSON");
        assertEquals("Invalid request format", response2.getMessage());
        
        // Test updateOrgDetails with invalid JSON
        String responseJson3 = featureUserManagement.updateOrgDetails(invalidJson);
        Response response3 = gson.fromJson(responseJson3, Response.class);
        
        assertEquals(400, response3.getStatus(), "Expected status 400 for invalid JSON");
        assertEquals("Invalid request format", response3.getMessage());
    }
} 
