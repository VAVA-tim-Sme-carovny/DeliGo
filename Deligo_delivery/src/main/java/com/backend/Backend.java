package com.backend;

import com.backend.features.FeatureValidateRequest;

public class Backend {
    private final FeatureValidateRequest featureValidateRequest;

    public Backend() {
        this.featureValidateRequest = new FeatureValidateRequest();  // Ensure it's initialized
    }

    public FeatureValidateRequest getFeatureValidateRequest() {
        return featureValidateRequest;
    }
}