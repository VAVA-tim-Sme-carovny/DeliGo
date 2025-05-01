package com.deligo.Frontend.Controllers.FeatureLogin;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Frontend.Controllers.Popups.*;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Utils.UTF8Control;
import com.sun.javafx.iio.gif.GIFImageLoader2;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.ResourceBundle;

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
    private ResourceBundle bundle;

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
    public void initialize() {
        this.bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault(), new UTF8Control());
    }
    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if(btnHome != null) btnHome.setOnAction(event -> {
            this.logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to main page");
            mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml", false);
            mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml", false);
            mainPageController.loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml", false);
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

                    if(user != null && !user.isEmpty() && role.equals("customer")){
                        mainPageController.loadMainContent("/Views/Content/OrderPanel/OrderContentPanel.fxml", false);
                        mainPageController.loadRightPanel("/Views/Content/OrderPanel/CartRightPanel.fxml", false);
                        mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
                        mainPageController.clearBottomPanel();
                    }else{
                        mainPageController.clearContentPanel();
                        mainPageController.loadControllerPanel("/Views/Controllers/EmployeeTopPanel.fxml", false);
                    }
//                    mainPageController.loadControllerPanel("/Views/Controllers/EmployeeTopPanelController.fxml");
//                    mainPageController.loadLeftPanel("/Views/Content/EmployeePanel/OrdersLeftPanel.fxml");
//                    mainPageController.clearContentPanel();
//                    mainPageController.clearBottomPanel();
                } else {
                    mainPageController.showWarningPopup(bundle.getString("popup.warning.message"), 500);
//                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Invalid credentials!");
//                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, response);
//                    mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
//                    mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
//                    mainPageController.loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml");
//                    mainPageController.clearRightPanel();
                }
            });
        }

    }
}
