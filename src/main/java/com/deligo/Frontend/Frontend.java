package com.deligo.Frontend;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.FrontendController;
import com.deligo.Frontend.Views.MainView;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.RestApi.RestAPIServer;

public class Frontend {

    private final RestAPIServer apiServer;
    private final LoggingAdapter logger;
    private final ConfigLoader config;

    // Controller a View
    private final FrontendController controller;
    private final MainView mainView;

    public Frontend(RestAPIServer apiServer, LoggingAdapter logger, ConfigLoader config) {
        this.apiServer = apiServer;
        this.logger = logger;
        this.config = config;

        // Nastavíme FrontendConfig do REST API
        apiServer.setFrontendConfig(new FrontendConfig(this));

        // Vytvoríme Controller
        controller = new FrontendController(this.apiServer, this.logger, this.config);
        controller.initializeFeatures();

        // Vytvoríme View a spustíme okno
        mainView = new MainView(this.controller, this.logger);
        mainView.launchWindow();
    }

    // Ak potrebuješ prístup k controlleru z iných častí
    public FrontendController getController() {
        return controller;
    }

    public MainView getMainView() {
        return mainView;
    }
}
