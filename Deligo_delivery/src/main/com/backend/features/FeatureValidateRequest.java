package main.com.backend.features;

import main.com.api.server.RestAPIServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeatureValidateRequest {
    private static final Logger logger = LogManager.getLogger(FeatureValidateRequest.class);
    private RestAPIServer restAPI;

    public FeatureValidateRequest(RestAPIServer restAPI) {
        this.restAPI = restAPI;
    }

    public String validateEmployeeRequest(Object data) {
        logger.info("Validating request: " + data.toString());
        boolean isValid = data.toString().contains("valid");
        return isValid ? "Validation success" : "Validation failed";
    }
}