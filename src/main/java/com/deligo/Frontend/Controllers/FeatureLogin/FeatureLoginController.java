package com.deligo.Frontend.Controllers.FeatureLogin;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
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


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;


    public FeatureLoginController(LoggingAdapter logger, MainPageController mainPageController) {
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
            mainPageController.clearRightPanel();
        });

        if (loginButton != null) {
            loginButton.setOnAction(event -> {

//             ‼️ Otvorim objednavkome menu bez logiky
                mainPageController.loadMainContent("/Views/Content/OrderPanel/OrderContentPanel.fxml");
                mainPageController.loadRightPanel("/Views/Content/OrderPanel/CartRightPanel.fxml");


                String username = usernameField.getText();
                String password = passwordField.getText();

                String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                // Pošli request (a čakaj na odpoveď)
                String response = mainPageController.getServer().sendPostRequest("/be/login/employee", json);

                logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Login response: " + response);

                // Môžeš parsovať JSON alebo overiť obsah:
                if (response.contains("\"status\":200")) {
                    logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.FRONTEND, "Login successful!");
                    // Ak chceš, napríklad prepni scénu:
                    mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
                    mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
                } else {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Login failed: " + response);
                }
            });
        }

//        openOrderMenu.setOnAction(event -> {
//            mainPageController.loadMainContent("/Views/Content/MainContentPanel.fxml");
//            mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
//        });
//
//        openRegisterWindowPopup.setOnAction(event -> {
//            mainPageController.loadMainContent("/Views/Content/MainContentPanel.fxml");
//            mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
//        });
//
//        loginButton.setOnAction(event -> {
//            System.out.println("prihlasenie");
//            mainPageController.loadMainContent("/Views/Content/MainContentPanel.fxml");
//            mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
//        });
    }
}
