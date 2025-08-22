package com.deligo.Backend.FeatureUserRegistration;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureUserRegistrationIntegrationTest extends com.deligo.Backend.BaseFeature.BaseIntegrationTest{
    private static Gson gson = new Gson();
    private FeatureUserRegister featureUserRegister;

    @BeforeEach
    void setUpFeature() {
        // Vytvoríme novú inštanciu FeatureOrgDetails s už inicializovanými závislosťami
        featureUserRegister = new FeatureUserRegister(configLoader, logger, restApiServer, new GenericDAO<>(User.class, "users"));
    }

    @AfterAll
    static void tearDown() {
        // Ak potrebuješ ukončiť server alebo iné zdroje, môžeš to urobiť tu
        // Napríklad restApiServer.stop() alebo podobne.
    }

    @Test
    void testCreateAccountValidInput() {
        String jsonData = "{\n" +
                "  \"username\": \"radkko\",\n" +
                "  \"password\": \"macock\",\n" +
                "  \"roles\": [\"admin\"],\n" +
                "  \"tag\": []\n" +
                "}";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertEquals("Údaje boli zapísané správne", response.getMessage());
    }

    @Test
    void testCreateAccountInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid JSON");
        assertTrue(response.getMessage().contains("Neplatný formát JSON"),
                "Expected error message for invalid JSON");
    }

    @Test
    void testCreateAccountInvalidRoles() {
        String jsonData = "{\n" +
                "  \"username\": \"radkko\",\n" +
                "  \"password\": \"macock\",\n" +
                "  \"roles\": [\"debilko\"],\n" +
                "  \"tag\": []\n" +
                "}";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid Roles");
        assertTrue(response.getMessage().contains("Nesprávna rola"), "Expected error message for invalid Roles");
    }

}