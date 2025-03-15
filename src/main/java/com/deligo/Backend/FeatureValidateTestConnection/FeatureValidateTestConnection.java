package com.deligo.Backend.FeatureValidateTestConnection;

import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;

public class FeatureValidateTestConnection {

    private final LoggingAdapter logger;
    private final RestAPIServer restApiServer;

    public FeatureValidateTestConnection(LoggingAdapter logger, RestAPIServer restApiServer) {
        this.logger = logger;
        this.restApiServer = restApiServer;
        this.runHealthCheck();
    }

    public String validateTestConnection(Object data) {
        this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Test connection received: " + data);

        if ("TEST_CONNECTION".equalsIgnoreCase(data.toString())) {
            this.logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, "Test connection validated successfully.");
            return "SUCCESS";
        } else {
            this.logger.log(LogType.WARNING, LogPriority.HIGH, LogSource.BECKEND, "Invalid test connection payload.");
            return "FAILED";
        }
    }

    private void runHealthCheck() {
        this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Running REST API health checks from Backend...");

        try {
            String postResponse = this.restApiServer.sendPostRequest("/health", "health-check");
            this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "POST /health response: " + postResponse);

            String getResponse = this.restApiServer.sendGetRequest("/health");
            this.logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "GET /health response: " + getResponse);

            if (postResponse.contains("OK") && getResponse.contains("OK")) {
                this.logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "REST API health checks passed from Backend!");
            } else {
                this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "REST API health checks failed from Backend!");
                throw new RuntimeException("REST API health check failed from Backend!");
            }

        } catch (Exception e) {
            this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Error during REST API health check from Backend: " + e.getMessage());
            throw new RuntimeException("REST API health check failed from Backend: " + e.getMessage());
        }
    }
}
