package com.deligo.RestApi;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.LoggingManager;
import com.deligo.RestApi.Utils.RequestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying SSL connection to the REST API server
 */
public class SSLConnectionTest {
    private static RestAPIServer server;
    private static LoggingAdapter logger;
    private static final int TEST_PORT = 8443;

    @BeforeAll
    public static void setUp() throws IOException {
        // Initialize logger
        LoggingManager.initialize();
        logger = LoggingManager.getAdapter();

        // Create a test config - using the main ConfigLoader
        // The DatabaseManager.utils.ConfigLoader will automatically load config.properties from test/resources
        ConfigLoader config = new ConfigLoader("src/test/resources/test-config.yaml");

        // Start the server
        server = new RestAPIServer(logger, config, TEST_PORT);

        System.out.println("[DEBUG_LOG] Server started on port " + TEST_PORT);
    }

    @AfterAll
    public static void tearDown() {
        if (server != null) {
            server.stop();
            System.out.println("[DEBUG_LOG] Server stopped");
        }
    }

    @Test
    @Disabled("Enable this test after running the generate-certificate.sh script to create the keystore")
    public void testSSLConnection() {
        try {
            // Test HTTPS connection to the health endpoint
            String baseUrl = "https://localhost:" + TEST_PORT + "/api";
            String response = RequestUtils.sendGetRequest(baseUrl + "/health", logger);

            System.out.println("[DEBUG_LOG] Response from server: " + response);

            // Verify that the response contains "OK"
            assertTrue(response.contains("OK"), "Response should contain 'OK'");

            // Verify that the connection is using HTTPS
            assertTrue(baseUrl.startsWith("https://"), "URL should use HTTPS protocol");

            System.out.println("[DEBUG_LOG] SSL connection test passed");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in SSL connection test: " + e.getMessage());
            fail("SSL connection test failed: " + e.getMessage());
        }
    }
}
