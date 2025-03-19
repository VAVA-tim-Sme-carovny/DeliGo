package com.deligo.RestApi;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.FrontendConfig;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Backend.BackendConfig;
import com.deligo.RestApi.CentralServer.ConsulRegistration;
import com.deligo.RestApi.Utils.HealthHandler;
import com.deligo.RestApi.Utils.NetworkUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RestAPIServer {

    private static BackendConfig backendConfig;
    private static FrontendConfig frontendConfig;
    private static LoggingAdapter logger;
    private static ConfigLoader deviceConfiguration;

    private final String BASE_URL = "http://localhost:8080/api";

    public RestAPIServer(LoggingAdapter adapter, ConfigLoader config) throws IOException {
        logger = adapter;
        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, "Starting REST API Server");

        deviceConfiguration = config;
        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, "Configuration was loaded");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api", new RequestHandler(this));  // Pass reference to THIS

//        server.createContext("/health", new HealthHandler(this));

        server.setExecutor(null);
        server.start();

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.REST_API, "REST API Server running on " + BASE_URL);
        runStartupTests();

//        ConsulRegistration.registerService(deviceConfiguration.getDeviceId(), NetworkUtils.getLocalIpAddress() , 8080, config.getLoginRole());
    }

    public static void setBackendConfig(BackendConfig config) {
        backendConfig = config;
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Backend Config injected");
    }

    public static void setFrontendConfig(FrontendConfig config) {
        frontendConfig = config;
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Frontend Config injected");
    }

    private void runStartupTests() {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Running REST API health checks...");

        try {
            String postResponse = sendPostRequest("/health", "health-check");
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /health response: " + postResponse);

            String getResponse = sendGetRequest("/health");
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET /health response: " + getResponse);

            if (postResponse.contains("OK") && getResponse.contains("OK")) {
                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.REST_API, "REST API health checks passed!");
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API, "REST API health checks failed!");
                throw new RuntimeException("REST API health check failed!");
            }

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API, "Error during health check: " + e.getMessage());
            throw new RuntimeException("REST API health check failed: " + e.getMessage());
        }
    }


    public String sendPostRequest(String endpoint, String jsonData) {
        String targetUrl = BASE_URL + endpoint;
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
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST to " + targetUrl + " responded with code: " + status);

            return readResponse(connection);

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API, "POST request to " + targetUrl + " failed: " + e.getMessage());
            return "POST request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String sendGetRequest(String endpoint) {
        String targetUrl = BASE_URL + endpoint;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET to " + targetUrl + " responded with code: " + status);

            return readResponse(connection);

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API, "GET request to " + targetUrl + " failed: " + e.getMessage());
            return "GET request failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Pomocná metóda na načítanie odpovede z HttpURLConnection
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

    public String handleHealthRequest(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET /health checked");
            return "GET /health OK!";
        } else if ("POST".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /health checked");
            return "POST /health OK!";
        } else {
            logger.log(LogType.WARNING, LogPriority.LOW, LogSource.REST_API, "Unsupported method on /health");
            return "Method not supported for /health";
        }
    }

    // 🚀 Tento handler spracováva prichádzajúce HTTP requesty
    public class RequestHandler implements HttpHandler {

        private final RestAPIServer restAPIServer;

        public RequestHandler(RestAPIServer server) {
            this.restAPIServer = server;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String route = exchange.getRequestURI().getPath();
            String response = "";

            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Received request: " + method + " " + route);

            if (route.equalsIgnoreCase("/api/health")) {
                response = restAPIServer.handleHealthRequest(method);
            } else if ("POST".equalsIgnoreCase(method)) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.REST_API, "Request body: " + requestBody);

                if (route.startsWith("/api/be")) {
                    response = (backendConfig != null) ? backendConfig.routePost(route, requestBody).toString() : "Backend not configured!";
                } else if (route.startsWith("/api/fe")) {
                    response = (frontendConfig != null) ? frontendConfig.routePost(route, requestBody).toString() : "Frontend not configured!";
                }
            } else if ("GET".equalsIgnoreCase(method)) {
                if (route.startsWith("/api/be")) {
                    response = (backendConfig != null) ? backendConfig.routeGet(route).toString() : "Backend not configured!";
                } else if (route.startsWith("/api/fe")) {
                    response = (frontendConfig != null) ? frontendConfig.routeGet(route).toString() : "Frontend not configured!";
                }
            } else {
                response = "Method not supported";
            }

            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

}
