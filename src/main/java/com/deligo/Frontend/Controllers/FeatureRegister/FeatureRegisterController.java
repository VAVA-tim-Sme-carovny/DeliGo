package com.deligo.Frontend.Controllers.FeatureRegister;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FeatureRegisterController implements InitializableWithParent {
    @FXML
    private Button btnHome;
    private LoggingAdapter logger;
    private MainPageController mainPageController;


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;


    public FeatureRegisterController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }
    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if(btnHome != null) btnHome.setOnAction(event -> {
            this.logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to main page");
            mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
            mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
            mainPageController.loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml");
            mainPageController.clearRightPanel();
        });

        if (registerButton != null) {
            registerButton.setOnAction(event -> {
                String username = usernameField.getText();
                String password = passwordField.getText();

                String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                String response = mainPageController.getServer().sendPostRequest("/be/register", json);

                logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Registration response: " + response);

                if (response.contains("\"status\":200")) {
                    logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "Login successful!");
                    mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
                    mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
                } else {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Registration failed!");
//                    mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
//                    mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
//                    mainPageController.loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml");
//                    mainPageController.clearRightPanel();
                }
            });
        }
    }
}
