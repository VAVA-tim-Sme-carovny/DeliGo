package com.deligo.Backend.FeatureUserLogin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureUserLoginIntegrationTest extends com.deligo.Backend.BaseFeature.BaseIntegrationTest{
    private static Gson gson = new Gson();
    private FeatureUserLogin featureUserLogin;

    @BeforeEach
    void setUpFeature() {
        // Vytvoríme novú inštanciu FeatureOrgDetails s už inicializovanými závislosťami
        featureUserLogin = new FeatureUserLogin(configLoader, logger, restApiServer, new GenericDAO<>(User.class, "users"));
    }

    @AfterAll
    static void tearDown() {
        // Ak potrebuješ ukončiť server alebo iné zdroje, môžeš to urobiť tu
        // Napríklad restApiServer.stop() alebo podobne.
    }

    @Test
    void loginEmployeeValidInput() {
        String jsonData = "{\n" +
                "  \"username\": \"radko\",\n" +
                "  \"password\": \"macock\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("Vitaj"),
                "Údaje pre prihlásenie sú správne.");
    }

    @Test
    void loginEmployeeInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void loginEmployeeInvalidUsername() {
        String jsonData = "{\n" +
                "  \"username\": \"radkko\",\n" +
                "  \"password\": \"macock\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid Username");
        assertTrue(response.getMessage().contains("nebol nájdený"),
                "Expected error message for username not found");
    }

}
