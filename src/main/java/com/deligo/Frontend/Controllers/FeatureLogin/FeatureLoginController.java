package com.deligo.Frontend.Controllers.FeatureLogin;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FeatureLoginController implements InitializableWithParent {



    @FXML
    private Button btnHome;
//    @FXML
//    private Button loginButton;
//    @FXML
//    private Button openOrderMenu;
//    @FXML
//    private Button openRegisterWindowPopup;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private ConfigLoader configLoader;


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;


    public FeatureLoginController(LoggingAdapter logger, MainPageController mainPageController, ConfigLoader configLoader) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.configLoader = configLoader;
    }
    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if(btnHome != null) btnHome.setOnAction(event -> {
            this.logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to main page");
            mainPageController.clearAll();
            mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
            mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
            mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
        });

        if (loginButton != null) {
            loginButton.setOnAction(event -> {
                String username = usernameField.getText();
                String password = passwordField.getText();

                String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                String response = mainPageController.getServer().sendPostRequest("/be/login/employee", json);

                logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Login response: " + response);

                if (response.contains("\"status\":200")) {
                    logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "Login successful!");
                    String user = configLoader.getConfigValue("login", "user", String.class);
                    String role = configLoader.getConfigValue("login", "role", String.class);

                     mainPageController.clearAll();

                     if (role != null) {
                            mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
                            mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
                            mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
                    }
                } else {
                    mainPageController.showWarningPopup("Invalid credentials", 500);
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Invalid credentials!");
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, response);
                }
            });
        }

    }
}
