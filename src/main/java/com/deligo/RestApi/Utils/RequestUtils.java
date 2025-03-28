package com.deligo.RestApi.Utils;

import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestUtils {

    public static String sendPostRequest(String targetUrl, String jsonData, LoggingAdapter logger) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            if (jsonData != null && !jsonData.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int status = connection.getResponseCode();
            logger.log(LogType.INFO, null, null, "POST to " + targetUrl + " responded with code: " + status);

            return readResponse(connection);

        } catch (Exception e) {
            logger.log(LogType.ERROR, null, null, "POST request to " + targetUrl + " failed: " + e.getMessage());
            return "POST request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String sendGetRequest(String targetUrl, LoggingAdapter logger) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            logger.log(LogType.INFO, null, null, "GET to " + targetUrl + " responded with code: " + status);

            return readResponse(connection);

        } catch (Exception e) {
            logger.log(LogType.ERROR, null, null, "GET request to " + targetUrl + " failed: " + e.getMessage());
            return "GET request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
