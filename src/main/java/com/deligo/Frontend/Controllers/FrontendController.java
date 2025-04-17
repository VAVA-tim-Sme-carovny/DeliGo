package com.deligo.Frontend.Controllers;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.FeatureTestCommunication.FeatureTestCommunication;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.RestApi.RestAPIServer;

/**
 * Controller, ktorý riadi logiku frontendu.
 * Vytvára featury a môže spracovávať udalosti z View.
 */
public class FrontendController {

    private final RestAPIServer apiServer;
    private final LoggingAdapter logger;
    private ConfigLoader config;

    private FeatureTestCommunication featureTestCommunication;

    public FrontendController(RestAPIServer apiServer, LoggingAdapter logger, ConfigLoader config) {
        this.apiServer = apiServer;
        this.logger = logger;
        this.config = config;
    }

    public void initializeFeatures() {
        featureTestCommunication = new FeatureTestCommunication(this.config, this.logger, this.apiServer);
        featureTestCommunication.testConnection();
    }

    public FeatureTestCommunication getFeatureTestCommunication() {
        return featureTestCommunication;
    }

}
