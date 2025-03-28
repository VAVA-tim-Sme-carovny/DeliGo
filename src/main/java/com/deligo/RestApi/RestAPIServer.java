package com.deligo.RestApi;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.FrontendConfig;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Backend.BackendConfig;
import com.deligo.RestApi.CentralServer.ConsulRegistration;
import com.deligo.RestApi.Handlers.*;
import com.deligo.RestApi.Utils.RequestUtils;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

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

        // Registrácia kontextov pre backend, frontend a health endpoint
        server.createContext("/api/be", new BackendHandler(this));
        server.createContext("/api/fe", new FrontendHandler(this));
        server.createContext("/api/health", new HealthHandler(this));

        server.setExecutor(null);
        server.start();

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.REST_API, "REST API Server running on " + BASE_URL);
        runStartupTests();

//        ConsulRegistration.registerService(deviceConfiguration.getDeviceId(), NetworkUtils.getLocalIpAddress(), 8080, config.getLoginRole());
    }

    public static void setBackendConfig(BackendConfig config) {
        backendConfig = config;
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Backend Config injected");
    }

    public static void setFrontendConfig(FrontendConfig config) {
        frontendConfig = config;
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Frontend Config injected");
    }

    public BackendConfig getBackendConfig() {
        return backendConfig;
    }

    public FrontendConfig getFrontendConfig() {
        return frontendConfig;
    }

    public LoggingAdapter getLogger() {
        return logger;
    }

    public ConfigLoader getConfig() {
        return deviceConfiguration;
    }

    /**
     * Testing send and get request funcitons
     */
    private void runStartupTests() {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Running REST API health checks...");

        try {
            String postResponse = RequestUtils.sendPostRequest("http://localhost:8080/api/health", "health-check", logger);
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /api/health response: " + postResponse);

            String getResponse = RequestUtils.sendGetRequest("http://localhost:8080/api/health", logger);
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET /api/health response: " + getResponse);

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


    public String sendPostRequest(String endpoint, String jsonData) {
        return RequestUtils.sendPostRequest(BASE_URL + endpoint, jsonData, logger);
    }

    public String sendGetRequest(String endpoint) {
        return RequestUtils.sendGetRequest(BASE_URL + endpoint, logger);
    }
}
