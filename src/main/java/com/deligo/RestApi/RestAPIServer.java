package com.deligo.RestApi;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.FrontendConfig;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Backend.BackendConfig;
import com.deligo.RestApi.CentralServer.ConsulRegistration;
import com.deligo.RestApi.Handlers.*;
import com.deligo.RestApi.Utils.NetworkUtils;
import com.deligo.RestApi.Utils.RequestUtils;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class RestAPIServer {

    private static BackendConfig backendConfig;
    private static FrontendConfig frontendConfig;
    private static LoggingAdapter logger;
    private static ConfigLoader deviceConfiguration;

    private String BASE_URL;
    private int port;
    private HttpServer server; // Can be either HttpServer or HttpsServer

    public RestAPIServer(LoggingAdapter adapter, ConfigLoader config) throws IOException {
        this(adapter, config, 8085);
    }

    public RestAPIServer(LoggingAdapter adapter, ConfigLoader config, int port) throws IOException {
        logger = adapter;
        this.port = port;

        deviceConfiguration = config;
        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, "Configuration was loaded");

        boolean sslEnabled = Boolean.parseBoolean(com.deligo.DatabaseManager.utils.ConfigLoader.get("SSL_ENABLED"));

        if (sslEnabled) {
            this.BASE_URL = "https://localhost:" + port + "/api";
            logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, "Starting REST API Server with SSL");

            try {
                // Set up the HTTPS server
                HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), 0);

                // Configure SSL
                SSLContext sslContext = createSSLContext();
                httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

                // Register contexts
                httpsServer.createContext("/api/be", new BackendHandler(this));
                httpsServer.createContext("/api/fe", new FrontendHandler(this));
                httpsServer.createContext("/api/health", new HealthHandler(this));

                httpsServer.setExecutor(null);
                httpsServer.start();

                server = httpsServer;
            } catch (Exception e) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API, "Failed to start HTTPS server: " + e.getMessage());
                throw new IOException("Failed to start HTTPS server", e);
            }
        } else {
            this.BASE_URL = "http://localhost:" + port + "/api";
            logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, "Starting REST API Server");

            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Register contexts
            server.createContext("/api/be", new BackendHandler(this));
            server.createContext("/api/fe", new FrontendHandler(this));
            server.createContext("/api/health", new HealthHandler(this));

            server.setExecutor(null);
            server.start();
        }

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.REST_API, "REST API Server running on " + BASE_URL);
        runStartupTests();

//        ConsulRegistration.registerService(
//                deviceConfiguration.getConfigValue("device","id", String.class),         // → "C5"
//                NetworkUtils.getLocalIpAddress(), port,
//                deviceConfiguration.getConfigValue("login","roles", String.class));
    }

    /**
     * Stops the HTTP server with the specified delay
     * @param seconds Delay in seconds before stopping
     */
    public void stop(int seconds) {
        if (server != null) {
            logger.log(LogType.INFO, LogPriority.HIGH, LogSource.REST_API, 
                      "Stopping REST API Server on port " + port + " with delay " + seconds + " seconds");
            server.stop(seconds);
        }
    }

    /**
     * Stops the HTTP server immediately
     */
    public void stop() {
        stop(0);
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

    public int getPort() {
        return port;
    }

    /**
     * Testing send and get request funcitons
     */
    private void runStartupTests() {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "Running REST API health checks...");

        try {
            String postResponse = RequestUtils.sendPostRequest(BASE_URL + "/health", "{\"message\":\"health-check\"}", logger);
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /api/health response: " + postResponse);

            String getResponse = RequestUtils.sendGetRequest(BASE_URL + "/health", logger);
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
        String updatedData = jsonData;
        if (!"TEST_CONNECTION".equalsIgnoreCase(jsonData) && jsonData != null) {
            updatedData = !jsonData.trim().startsWith("{") && !jsonData.trim().endsWith("}") ? "{message:" + jsonData + "}" : jsonData;

        }
        logger.log(LogType.INFO,LogPriority.HIGH, LogSource.REST_API, "Sending POST request to " + BASE_URL + endpoint + " with JSON data: " + updatedData);
        return RequestUtils.sendPostRequest(BASE_URL + endpoint, updatedData, logger);
    }

    public String sendGetRequest(String endpoint) {
        return RequestUtils.sendGetRequest(BASE_URL + endpoint, logger);
    }

    /**
     * Creates an SSL context for HTTPS connections
     * @return SSLContext configured with the keystore
     * @throws Exception if there's an error creating the SSL context
     */
    private SSLContext createSSLContext() throws Exception {
        // Load keystore
        String keystorePath = com.deligo.DatabaseManager.utils.ConfigLoader.get("SSL_KEYSTORE_PATH");
        String keystorePassword = com.deligo.DatabaseManager.utils.ConfigLoader.get("SSL_KEYSTORE_PASSWORD");

        if (keystorePath == null || keystorePassword == null) {
            throw new IllegalStateException("SSL_KEYSTORE_PATH or SSL_KEYSTORE_PASSWORD not found in configuration");
        }

        char[] passwordChars = keystorePassword.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, passwordChars);
        }

        // Create key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, passwordChars);

        // Create trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create SSL context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }
}
