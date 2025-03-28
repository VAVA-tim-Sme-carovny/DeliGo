package com.deligo.Backend.FeatureOrganizationDetails;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.DatabaseManager;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.LoggingManager;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureOrgDetailsIntegrationTest extends com.deligo.Backend.BaseFeature.BaseIntegrationTest {

    private static Gson gson = new Gson();
    private FeatureOrgDetails featureOrgDetails;

    @BeforeEach
    void setUpFeature() {
        // Vytvoríme novú inštanciu FeatureOrgDetails s už inicializovanými závislosťami
        featureOrgDetails = new FeatureOrgDetails(configLoader, logger, restApiServer, dbManager);
    }

    @AfterAll
    static void tearDown() {
        // Ak potrebuješ ukončiť server alebo iné zdroje, môžeš to urobiť tu
        // Napríklad restApiServer.stop() alebo podobne.
    }

    @Test
    void testUpdateOrgDetailsValidInput() {
        String jsonData = "{"
                + "\"openingTimes\": [[\"08:30\", \"17:00\"], [\"09:00\", \"18:00\"]],"
                + "\"phoneNumber\": \"09111 222 333\","
                + "\"mail\": \"test@example.com\""
                + "}";
        String responseJson = featureOrgDetails.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertEquals("Údaje boli zapísané správne", response.getMessage());
    }

    @Test
    void testUpdateOrgDetailsInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureOrgDetails.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Invalid JSON format"),
                "Expected error message for invalid JSON");
    }

    @Test
    void testUpdateOrgDetailsInvalidPhone() {
        String jsonData = "{"
                + "\"openingTimes\": [[\"08:30\", \"17:00\"]],"
                + "\"phoneNumber\": \"12345\","
                + "\"mail\": \"test@example.com\""
                + "}";
        String responseJson = featureOrgDetails.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid phone");
        assertTrue(response.getMessage().contains("Telefónne číslo je zadané v nesprávnom formáte"),
                "Expected error message for invalid phone");
    }

    @Test
    void testUpdateOrgDetailsInvalidEmail() {
        String jsonData = "{"
                + "\"openingTimes\": [[\"08:30\", \"17:00\"]],"
                + "\"phoneNumber\": \"09111 222 333\","
                + "\"mail\": \"invalid-email\""
                + "}";
        String responseJson = featureOrgDetails.updateOrgDetails(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid email");
        assertTrue(response.getMessage().contains("Email je zadaný v nesprávnom formáte"),
                "Expected error message for invalid email");
    }
}