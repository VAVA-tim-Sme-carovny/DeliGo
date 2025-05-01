package com.deligo.Backend.FeatureUserLogin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.DeviceLoginResponse;
import com.deligo.Model.LoginResponse;
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
        featureUserLogin = new FeatureUserLogin(configLoader, logger, restApiServer);
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
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("Vitaj"),
                "Údaje pre prihlásenie sú správne.");
    }

    @Test
    void loginEmployeeInvalidJSON() {
        String jsonData = "invalid json";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

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
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for invalid Username");
        assertTrue(response.getMessage().contains("nebol nájdený"),
                "Expected error message for username not found");
    }

    @Test
    void loginEmployeeWrongPassword() {
        String jsonData = "{\n" +
                "  \"username\": \"radko\",\n" +
                "  \"password\": \"nemacock\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(500, response.getStatus(), "Expected status 500 for wrong password");
        assertTrue(response.getMessage().contains("Neplatné prihlasovacie údaje"),
                "Expected error message for wrong password");
    }

    @Test
    void loginCustomerValidInput() {
        String responseJson = featureUserLogin.loginCustomer();
        DeviceLoginResponse response = gson.fromJson(responseJson, DeviceLoginResponse.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("Vitaj!"),
                "Údaje pre prihlásenie sú správne.");
    }

    @Test
    void logoutEmployee(){
        String responseJson = featureUserLogin.logout();
        DeviceLoginResponse response = gson.fromJson(responseJson, DeviceLoginResponse.class);

        assertEquals(200, response.getStatus(), "Expected status 200 for valid input");
        assertTrue(response.getMessage().contains("Úspešné odhlásenie!"),
                "Používateľ bol odhlásený.");
    }

    @Test
    void loginEmployeeEmptyUsername() {
        String jsonData = "{\n" +
                "  \"username\": \"\",\n" +
                "  \"password\": \"heslo\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(500, response.getStatus());
        assertTrue(response.getMessage().contains("Používateľ  nebol nájdený"));
    }


    @Test
    void loginEmployeeMissingPasswordField() {
        String jsonData = "{\n" +
                "  \"username\": \"radko\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(500, response.getStatus());
        assertTrue(response.getMessage().contains("Neplatný formát JSON: %s"));
    }

    @Test
    void loginEmployeeMissingUserField() {
        String jsonData = "{\n" +
                "  \"username\": \"radko\"\n" +
                "}";
        String responseJson = featureUserLogin.loginEmployee(jsonData);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        assertEquals(500, response.getStatus());
        assertTrue(response.getMessage().contains("Neplatný formát JSON: %s"));
    }



}
