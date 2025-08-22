package com.deligo.Frontend.Controllers;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
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
    private MainPageController mainPageController;

    public FrontendController(RestAPIServer apiServer, LoggingAdapter logger, ConfigLoader config) {
        this.apiServer = apiServer;
        this.logger = logger;
        this.config = config;
    }

    public void initializeFeatures() {
        featureTestCommunication = new FeatureTestCommunication(this.config, this.logger, this.apiServer);
        featureTestCommunication.testConnection();

        mainPageController = new MainPageController(this.config, this.logger, this.apiServer);
    }

    public FeatureTestCommunication getFeatureTestCommunication() {
        return featureTestCommunication;
    }


    public ConfigLoader getConfig() {
        return config;
    }

    public RestAPIServer getApiServer() {
        return apiServer;
    }

}
