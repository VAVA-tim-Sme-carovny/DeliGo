package com.backend;

import com.api.server.RestAPIServer;
import com.backend.features.FeatureValidateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Backend {
    private static final Logger logger = LogManager.getLogger(Backend.class);
    private final FeatureValidateRequest featureValidateRequest;

    public Backend(RestAPIServer restAPI) {
        featureValidateRequest = new FeatureValidateRequest(restAPI);
        Config.initialize(this);
        restAPI.setBackendConfig(Config.getInstance());
    }

    public FeatureValidateRequest getFeatureValidateRequest() {
        return featureValidateRequest;
    }
}