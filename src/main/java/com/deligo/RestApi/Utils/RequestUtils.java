package com.deligo.RestApi.Utils;

import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.BasicModels.LogType;
import org.json.JSONException;
import org.json.JSONObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestUtils {
    static {
        // For development only - disable certificate validation
        // Remove this in production and use proper certificate validation
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Disable hostname verification
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates JSON data using Hibernate Validator
     * 
     * @param jsonData The JSON data to validate
     * @param logger Logger for recording validation issues
     * @return Validated JSON string or null if validation fails
     */
    private static String validateJsonData(String jsonData, LoggingAdapter logger) {
        if (jsonData == null || jsonData.isEmpty()) {
            return jsonData; // Nothing to validate
        }

        try {
            // First, check if it's valid JSON
            new JSONObject(jsonData);

            // Use Hibernate Validator to validate the JSON string
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            JsonData data = new JsonData(jsonData);
            Set<ConstraintViolation<JsonData>> violations = validator.validate(data);

            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("JSON validation failed: ");
                for (ConstraintViolation<JsonData> violation : violations) {
                    errorMessage.append(violation.getMessage()).append("; ");
                }
                logger.log(LogType.ERROR, null, null, errorMessage.toString());
                return null;
            }

            return jsonData; // Return the original JSON if validation passes
        } catch (JSONException e) {
            logger.log(LogType.ERROR, null, null, "Invalid JSON format: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.log(LogType.ERROR, null, null, "Unexpected error during JSON validation: " + e.getMessage());
            return null;
        }
    }

    public static String sendPostRequest(String targetUrl, String jsonData, LoggingAdapter logger) {
        final int TIMEOUT = 10000;
        HttpURLConnection connection = null;
        try {
            // Validate JSON data before sending
            String validatedJsonData = jsonData;
            if (!"TEST_CONNECTION".equalsIgnoreCase(jsonData)) {
                validatedJsonData = validateJsonData(jsonData, logger);
            }
            if (jsonData != null && validatedJsonData == null) {
                logger.log(LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.REST_API, "POST request to " + targetUrl + " aborted due to invalid JSON data");
                return "POST request failed: Invalid JSON data";
            }

            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            if (validatedJsonData != null && !validatedJsonData.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = validatedJsonData.getBytes(StandardCharsets.UTF_8);
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
