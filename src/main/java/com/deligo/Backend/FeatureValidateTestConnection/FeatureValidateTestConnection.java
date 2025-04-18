package com.deligo.Backend.FeatureValidateTestConnection;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import org.json.JSONObject;

public class FeatureValidateTestConnection extends BaseFeature {

    public FeatureValidateTestConnection(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.runHealthCheck();
    }

    public String validateTestConnection(Object data) {
        this.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Test connection received: " + data);

        if ("TEST_CONNECTION".equalsIgnoreCase(data.toString())) {
            this.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, "Test connection validated successfully.");
            return "SUCCESS";
        } else {
            this.log(LogType.WARNING, LogPriority.HIGH, LogSource.BECKEND, "Invalid test connection payload.");
            return "FAILED";
        }
    }


    private void runHealthCheck() {
        this.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Running REST API health checks from Backend...");

        try {
            String postResponse = this.server.sendPostRequest("/health", "health-check");
            this.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "POST /health response: " + postResponse);

            String getResponse = this.server.sendGetRequest("/health");
            this.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "GET /health response: " + getResponse);

            if (postResponse.contains("OK") && getResponse.contains("OK")) {
                this.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "REST API health checks passed from Backend!");
            } else {
                this.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "REST API health checks failed from Backend!");
                throw new RuntimeException("REST API health check failed from Backend!");
            }

        } catch (Exception e) {
            this.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Error during REST API health check from Backend: " + e.getMessage());
            throw new RuntimeException("REST API health check failed from Backend: " + e.getMessage());
        }
    }


    public String testLogin(Object data) {
        this.log(LogType.INFO, LogPriority.HIGH, LogSource.BECKEND, "Login request received: " + data);

        // Simulovaný dummy response
        JSONObject response = new JSONObject();
        response.put("username", "demoUser");
        response.put("roles", new String[]{"admin", "user"});
        response.put("message", "Vitaj demoUser");
        response.put("status", 200);

        this.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "Returning login response: " + response.toString());

        return response.toString();
    }

}
