package com.deligo.Frontend.FeatureTestCommunication;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;

public class FeatureTestCommunication extends BaseFeature {

    private final LoggingAdapter logger;

    public FeatureTestCommunication(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.logger = logger;
        this.runHealthCheck();
    }

    public void testConnection() {
        this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Sending test connection request to Frontend...");

        // Celá adresa REST API servera
        String fullUrl = "/be/testConnection";
        String payload = "TEST_CONNECTION";

        // Odoslanie POST požiadavky
        String response = this.server.sendPostRequest(fullUrl, payload);

        if ("SUCCESS".equalsIgnoreCase(response)) {
            this.logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "FE -> BE communication test passed!");
        } else {
            this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "FE -> BE communication test failed! Response: " + response);
        }
    }

    private void runHealthCheck() {
        this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Running REST API health checks from Frontend...");

        try {
            String postResponse = this.server.sendPostRequest("/health", "health-check");
            this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "POST /health response: " + postResponse);

            String getResponse = this.server.sendGetRequest("/health");
            this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "GET /health response: " + getResponse);

            if (postResponse.contains("OK") && getResponse.contains("OK")) {
                this.logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "REST API health checks passed from Frontend!");
            } else {
                this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "REST API health checks failed from Frontend!");
                throw new RuntimeException("REST API health check failed from Frontend!");
            }

        } catch (Exception e) {
            this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Error during REST API health check from Frontend: " + e.getMessage());
            throw new RuntimeException("REST API health check failed from Frontend:" + e.getMessage());
        }
}
}