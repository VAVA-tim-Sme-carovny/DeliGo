package com.deligo.RestApi.CentralServer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ConsulRegistration {

    private static final String CONSUL_URL = "http://51.20.44.219/register"; // už bez login:password!
    private static final String CREDENTIALS = "credentials.yaml";

    public static void registerService(String deviceId, String deviceAddress, int port, String role) {
        try {
            String jsonPayload = String.format(
                    "{ \"Name\": \"%s\", \"Address\": \"%s\", \"Port\": %d, \"Tags\": [\"role=%s\"], \"Check\": { \"HTTP\": \"http://%s:%d/health\", \"Interval\": \"10s\" } }",
                    deviceId, deviceAddress, port, role, deviceAddress, port
            );

            URL url = new URL(CONSUL_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");

            // Pridať Basic Auth hlavičku
            String userCredentials = "deligoUser:deligoUser";
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Consul registration response code: " + responseCode);

            if (responseCode == 200) {
                System.out.println("Successfully registered service to Consul: " + deviceId);
            } else {
                System.err.println("Failed to register service to Consul. Response code: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("Error registering service to Consul: " + e.getMessage());
        }
    }
}
