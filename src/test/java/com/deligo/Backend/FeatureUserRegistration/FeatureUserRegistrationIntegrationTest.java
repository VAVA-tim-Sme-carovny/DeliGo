package com.deligo.Backend.FeatureUserRegistration;

import com.deligo.Backend.FeatureUserRegister.FeatureUserRegister;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.LoggingManager;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
                "  \"username\": \"Mieernko\",\n" +
                "  \"password\": \"vodnaveza\",\n" +
                "  \"roles\": [\"CHANGE_ORDER_STATE\"],\n" +
                "  \"tag\": []\n" +
                "}";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertEquals("Používateľ bol úspešne registrovaný", response.getMessage());
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
    void testCreateAccountNoPassword() {
        String jsonData = "{\n" +
                "  \"username\": \"Minko\",\n" +
                "  \"roles\": [\"CHANGE_ORDER_STATE\"],\n" +
                "  \"tag\": []\n" +
                "}";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for missing password");
        assertTrue(response.getMessage().contains("Heslo nebolo poskytnuté"),
                "Expected error message for missing password");

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





    @Test
    void testCreateAccountMissingUsername() {
        String jsonData = "{\n" +
                "  \"password\": \"tajneheslo\",\n" +
                "  \"roles\": [\"CHANGE_ORDER_STATE\"],\n" +
                "  \"tag\": []\n" +
                "}";
        String responseJson = featureUserRegister.createAccount(jsonData);
        Response response = gson.fromJson(responseJson, Response.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for missing username");
        assertTrue(response.getMessage().contains("Chyba pri ukladaní do databázyChyba pri vkladaní entity"),
                "Expected error message for missing username");
    }




}
